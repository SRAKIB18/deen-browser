package com.deenbrowser.app.browser

import android.app.Application
import android.graphics.Rect
import android.graphics.RectF
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.deenbrowser.app.core.utils.Constants
import com.deenbrowser.app.core.utils.UrlUtils
import com.deenbrowser.app.data.models.Bookmark
import com.deenbrowser.app.data.models.DetectionMode
import com.deenbrowser.app.data.models.HistoryItem
import com.deenbrowser.app.data.models.ProtectionSettings
import com.deenbrowser.app.data.models.TabItem
import com.deenbrowser.app.protection.FaceProtectionEngine
import com.deenbrowser.app.protection.ImageProcessingManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BrowserViewModel(application: Application) : AndroidViewModel(application) {

    val repository = BrowserRepository(application)
    val tabManager = TabManager()
    val downloadManagerHelper = DownloadManagerHelper(application)
    val faceProtectionEngine = FaceProtectionEngine(viewModelScope)
    val imageProcessingManager = ImageProcessingManager(viewModelScope)

    var onBlurImageRequested: ((src: String, mode: String, blurRadius: Float) -> Unit)? = null

    val tabs: StateFlow<List<TabItem>> = tabManager.tabs
    val activeTabIndex: StateFlow<Int> = tabManager.activeTabIndex

    val detectedFaceRects: StateFlow<List<Rect>> = faceProtectionEngine.detectedFaceRects
    val bitmapWidth: StateFlow<Int> = faceProtectionEngine.bitmapWidth
    val bitmapHeight: StateFlow<Int> = faceProtectionEngine.bitmapHeight

    val mappedOverlayRects: StateFlow<List<RectF>> = imageProcessingManager.mappedOverlayRects

    private val _currentUrlInput = MutableStateFlow(Constants.DEFAULT_HOME_URL)
    val currentUrlInput: StateFlow<String> = _currentUrlInput.asStateFlow()

    private val _isBookmarked = MutableStateFlow(false)
    val isBookmarked: StateFlow<Boolean> = _isBookmarked.asStateFlow()

    val protectionSettings: StateFlow<ProtectionSettings> = repository.protectionSettings
        .let { flow ->
            val state = MutableStateFlow(ProtectionSettings())
            viewModelScope.launch {
                flow.collectLatest { state.value = it }
            }
            state.asStateFlow()
        }

    val bookmarks: StateFlow<List<Bookmark>> = repository.allBookmarks
        .let { flow ->
            val state = MutableStateFlow<List<Bookmark>>(emptyList())
            viewModelScope.launch {
                flow.collectLatest { state.value = it }
            }
            state.asStateFlow()
        }

    val history: StateFlow<List<HistoryItem>> = repository.allHistory
        .let { flow ->
            val state = MutableStateFlow<List<HistoryItem>>(emptyList())
            viewModelScope.launch {
                flow.collectLatest { state.value = it }
            }
            state.asStateFlow()
        }

    val activeTab: TabItem?
        get() = tabManager.activeTab

    init {
        imageProcessingManager.onFacesFoundInImage = { src, hasFaces ->
            if (hasFaces) {
                val settings = protectionSettings.value
                val modeName = settings.detectionMode.name
                val radius = settings.blurRadius
                viewModelScope.launch(Dispatchers.Main) {
                    onBlurImageRequested?.invoke(src, modeName, radius)
                }
            }
        }
    }

    fun startProtectionLoop(webViewGetter: () -> WebView?) {
        faceProtectionEngine.startProtectionLoop(webViewGetter) { protectionSettings.value }
    }

    fun triggerFrameScan(webViewGetter: () -> WebView?) {
        faceProtectionEngine.triggerFrameScan(webViewGetter) { protectionSettings.value }
    }

    fun loadUrlOrSearch(input: String) {
        val formatted = UrlUtils.formatUrlOrSearch(input)
        _currentUrlInput.value = formatted
        tabManager.updateActiveTabUrl(formatted)
    }

    fun onUrlInputChanged(newInput: String) {
        _currentUrlInput.value = newInput
    }

    fun onPageStarted(url: String) {
        _currentUrlInput.value = url
        tabManager.updateActiveTabState(
            progress = 10,
            isLoading = true,
            isSecure = UrlUtils.isSecure(url)
        )
    }

    fun onPageFinished(url: String, title: String?) {
        val finalTitle = title ?: UrlUtils.extractDomain(url)
        _currentUrlInput.value = url
        tabManager.updateActiveTabUrl(url, finalTitle)
        tabManager.updateActiveTabState(
            progress = 100,
            isLoading = false,
            title = finalTitle,
            isSecure = UrlUtils.isSecure(url)
        )

        val currentTab = activeTab
        if (currentTab != null && !currentTab.isIncognito) {
            viewModelScope.launch {
                repository.addHistoryItem(finalTitle, url)
                checkIsBookmarked(url)
            }
        }
    }

    fun onProgressChanged(progress: Int) {
        tabManager.updateActiveTabState(
            progress = progress,
            isLoading = progress < 100
        )
    }

    fun updateNavigationState(canGoBack: Boolean, canGoForward: Boolean) {
        tabManager.updateActiveTabState(
            canGoBack = canGoBack,
            canGoForward = canGoForward
        )
    }

    fun toggleDesktopMode() {
        val currentTab = activeTab ?: return
        val newDesktopMode = !currentTab.isDesktopMode
        var currentUrl = currentTab.url

        if (newDesktopMode) {
            if (currentUrl.contains("://m.")) {
                currentUrl = currentUrl.replace("://m.", "://www.")
            } else if (currentUrl.contains("://mobile.")) {
                currentUrl = currentUrl.replace("://mobile.", "://www.")
            }
        } else {
            if (currentUrl.contains("://www.facebook.com")) {
                currentUrl = currentUrl.replace("://www.facebook.com", "://m.facebook.com")
            } else if (currentUrl.contains("://www.youtube.com")) {
                currentUrl = currentUrl.replace("://www.youtube.com", "://m.youtube.com")
            }
        }

        _currentUrlInput.value = currentUrl
        tabManager.updateActiveTabUrl(currentUrl)
        tabManager.updateActiveTabState(isDesktopMode = newDesktopMode)
    }

    fun toggleBookmark() {
        val currentTab = activeTab ?: return
        val url = currentTab.url
        val title = currentTab.title
        viewModelScope.launch {
            if (_isBookmarked.value) {
                repository.removeBookmark(url)
                _isBookmarked.value = false
            } else {
                repository.addBookmark(title, url)
                _isBookmarked.value = true
            }
        }
    }

    private suspend fun checkIsBookmarked(url: String) {
        _isBookmarked.value = repository.isBookmarked(url)
    }

    fun createTab(url: String = Constants.DEFAULT_HOME_URL, isIncognito: Boolean = false) {
        val newTab = tabManager.createNewTab(url, isIncognito)
        _currentUrlInput.value = newTab.url
    }

    fun selectTab(index: Int) {
        tabManager.selectTab(index)
        activeTab?.let {
            _currentUrlInput.value = it.url
        }
    }

    fun closeTab(index: Int) {
        tabManager.closeTab(index)
        activeTab?.let {
            _currentUrlInput.value = it.url
        }
    }

    fun closeAllTabs() {
        tabManager.closeAllTabs()
        _currentUrlInput.value = Constants.DEFAULT_HOME_URL
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteHistoryItem(id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun clearBookmarks() {
        viewModelScope.launch {
            repository.clearBookmarks()
        }
    }

    override fun onCleared() {
        super.onCleared()
        faceProtectionEngine.close()
        imageProcessingManager.close()
    }

    inner class DeenJSBridge {
        @JavascriptInterface
        fun onImagesDiscovered(jsonData: String) {
            if (protectionSettings.value.isMasterEnabled && protectionSettings.value.detectionMode != DetectionMode.OFF) {
                imageProcessingManager.processDiscoveredImagesJson(jsonData)
            }
        }
    }
}