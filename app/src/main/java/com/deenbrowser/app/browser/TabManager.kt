package com.deenbrowser.app.browser

import com.deenbrowser.app.core.utils.Constants
import com.deenbrowser.app.data.models.TabItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TabManager {

    private val _tabs = MutableStateFlow<List<TabItem>>(emptyList())
    val tabs: StateFlow<List<TabItem>> = _tabs.asStateFlow()

    private val _activeTabIndex = MutableStateFlow(0)
    val activeTabIndex: StateFlow<Int> = _activeTabIndex.asStateFlow()

    init {
        // Initialize with default home tab
        createNewTab(Constants.DEFAULT_HOME_URL)
    }

    val activeTab: TabItem?
        get() = _tabs.value.getOrNull(_activeTabIndex.value)

    fun createNewTab(url: String = Constants.DEFAULT_HOME_URL, isIncognito: Boolean = false): TabItem {
        val newTab = TabItem(
            url = url,
            isIncognito = isIncognito
        )
        val currentList = _tabs.value.toMutableList()
        currentList.add(newTab)
        _tabs.value = currentList
        _activeTabIndex.value = currentList.size - 1
        return newTab
    }

    fun selectTab(index: Int) {
        if (index in 0 until _tabs.value.size) {
            _activeTabIndex.value = index
        }
    }

    fun selectTabById(tabId: String) {
        val index = _tabs.value.indexOfFirst { it.id == tabId }
        if (index != -1) {
            _activeTabIndex.value = index
        }
    }

    fun closeTab(index: Int) {
        val currentList = _tabs.value.toMutableList()
        if (index in 0 until currentList.size) {
            currentList.removeAt(index)
            if (currentList.isEmpty()) {
                // If closed last tab, create fresh home tab
                val homeTab = TabItem(url = Constants.DEFAULT_HOME_URL)
                currentList.add(homeTab)
                _activeTabIndex.value = 0
            } else {
                if (_activeTabIndex.value >= currentList.size) {
                    _activeTabIndex.value = currentList.size - 1
                }
            }
            _tabs.value = currentList
        }
    }

    fun closeAllTabs() {
        val homeTab = TabItem(url = Constants.DEFAULT_HOME_URL)
        _tabs.value = listOf(homeTab)
        _activeTabIndex.value = 0
    }

    fun updateActiveTabUrl(url: String, title: String? = null) {
        val index = _activeTabIndex.value
        val list = _tabs.value.toMutableList()
        if (index in 0 until list.size) {
            val updated = list[index].copy(
                url = url,
                title = title ?: list[index].title
            )
            list[index] = updated
            _tabs.value = list
        }
    }

    fun updateActiveTabState(
        title: String? = null,
        progress: Int? = null,
        isLoading: Boolean? = null,
        canGoBack: Boolean? = null,
        canGoForward: Boolean? = null,
        isSecure: Boolean? = null,
        isDesktopMode: Boolean? = null
    ) {
        val index = _activeTabIndex.value
        val list = _tabs.value.toMutableList()
        if (index in 0 until list.size) {
            val current = list[index]
            val updated = current.copy(
                title = title ?: current.title,
                progress = progress ?: current.progress,
                isLoading = isLoading ?: current.isLoading,
                canGoBack = canGoBack ?: current.canGoBack,
                canGoForward = canGoForward ?: current.canGoForward,
                isSecure = isSecure ?: current.isSecure,
                isDesktopMode = isDesktopMode ?: current.isDesktopMode
            )
            list[index] = updated
            _tabs.value = list
        }
    }
}