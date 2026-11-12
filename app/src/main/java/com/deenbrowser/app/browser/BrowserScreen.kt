package com.deenbrowser.app.browser

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.deenbrowser.app.core.utils.Constants
import com.deenbrowser.app.data.models.DetectionMode
import com.deenbrowser.app.data.models.ProtectionSettings
import com.deenbrowser.app.protection.FaceBlurOverlayCanvas
import com.deenbrowser.app.ui.theme.DeenForestPrimary
import com.deenbrowser.app.ui.theme.DeenGoldAccent
import com.deenbrowser.app.ui.theme.DeenMintAccent

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserScreen(
    viewModel: BrowserViewModel,
    onNavigateHome: () -> Unit,
    onOpenTabSwitcher: () -> Unit,
    onOpenProtectionDashboard: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenDownloads: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    val tabs by viewModel.tabs.collectAsState()
    val activeTabIndex by viewModel.activeTabIndex.collectAsState()
    val activeTab = tabs.getOrNull(activeTabIndex)
    val isBookmarked by viewModel.isBookmarked.collectAsState()
    val protectionSettings by viewModel.protectionSettings.collectAsState()
    val currentUrlInput by viewModel.currentUrlInput.collectAsState()

    val mappedOverlayRects by viewModel.mappedOverlayRects.collectAsState()

    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.onBlurImageRequested = { src, mode, radius ->
            val escapedSrc = src.replace("'", "\\'")
            webViewRef?.evaluateJavascript("if(window.DeenApplyBlurToImage) window.DeenApplyBlurToImage('$escapedSrc', '$mode', $radius);", null)
        }
        viewModel.startProtectionLoop { webViewRef }
    }

    // Intercept hardware back button for WebView navigation
    BackHandler(enabled = webViewRef?.canGoBack() == true) {
        webViewRef?.goBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Toolbar & Address Bar
        Surface(
            tonalElevation = 4.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateHome) {
                        Icon(imageVector = Icons.Default.Home, contentDescription = "Home", tint = DeenForestPrimary)
                    }

                    // Address TextField with SSL & Protection indicator
                    OutlinedTextField(
                        value = currentUrlInput,
                        onValueChange = { viewModel.onUrlInputChanged(it) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        leadingIcon = {
                            val isSecure = activeTab?.isSecure ?: true
                            Icon(
                                imageVector = if (isSecure) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = if (isSecure) "Secure HTTPS" else "Insecure",
                                tint = if (isSecure) DeenForestPrimary else Color.Red,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Shield status indicator
                                Box(
                                    modifier = Modifier
                                        .padding(end = 6.dp)
                                        .clip(CircleShape)
                                        .background(if (protectionSettings.isMasterEnabled) DeenForestPrimary else Color.Gray)
                                        .clickable { onOpenProtectionDashboard() }
                                        .padding(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = "Protection Settings",
                                        tint = DeenGoldAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedBorderColor = DeenForestPrimary,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                        keyboardActions = KeyboardActions(onGo = {
                            viewModel.loadUrlOrSearch(currentUrlInput)
                        })
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(onClick = { webViewRef?.reload() }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }

                // Loading Progress Bar
                if (activeTab?.isLoading == true) {
                    LinearProgressIndicator(
                        progress = { (activeTab.progress / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth(),
                        color = DeenForestPrimary,
                    )
                }
            }
        }

        // Main WebView Viewport with Native SwipeRefreshLayout and AI Face Blur Overlay
        Box(modifier = Modifier.weight(1f)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    SwipeRefreshLayout(ctx).apply {
                        setColorSchemeColors(android.graphics.Color.parseColor("#1B4D3E"))
                        val webView = WebView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )

                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                databaseEnabled = true
                                useWideViewPort = activeTab?.isDesktopMode == true
                                loadWithOverviewMode = activeTab?.isDesktopMode == true
                                builtInZoomControls = true
                                displayZoomControls = false
                                setSupportZoom(true)
                                mediaPlaybackRequiresUserGesture = false
                                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                                userAgentString = if (activeTab?.isDesktopMode == true) Constants.DESKTOP_USER_AGENT else Constants.MOBILE_USER_AGENT
                            }

                            CookieManager.getInstance().setAcceptCookie(true)
                            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

                            addJavascriptInterface(viewModel.DeenJSBridge(), "DeenJSBridge")

                            webViewClient = DeenWebViewClient(
                                context = ctx,
                                isDesktopMode = { activeTab?.isDesktopMode == true },
                                onPageStarted = { url -> viewModel.onPageStarted(url) },
                                onPageFinished = { url, title ->
                                    viewModel.onPageFinished(url, title)
                                    viewModel.triggerFrameScan { this }
                                },
                                onUrlChanged = { url ->
                                    viewModel.updateNavigationState(canGoBack(), canGoForward())
                                }
                            )

                            webChromeClient = DeenWebChromeClient(
                                onProgressChanged = { progress -> viewModel.onProgressChanged(progress) },
                                onTitleReceived = { title -> viewModel.onPageFinished(url ?: "", title) },
                                onFaviconReceived = { favicon -> }
                            )

                            setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
                                viewModel.downloadManagerHelper.downloadFile(url, userAgent, contentDisposition, mimeType)
                            }

                            activeTab?.url?.let { loadUrl(it) }
                        }

                        addView(webView)
                        webViewRef = webView

                        setOnRefreshListener {
                            webView.reload()
                        }
                    }
                },
                update = { swipeRefresh ->
                    swipeRefresh.isRefreshing = activeTab?.isLoading == true
                    val webView = webViewRef ?: return@AndroidView
                    val isDesktop = activeTab?.isDesktopMode == true
                    val desiredUserAgent = if (isDesktop) Constants.DESKTOP_USER_AGENT else Constants.MOBILE_USER_AGENT

                    webView.settings.useWideViewPort = isDesktop
                    webView.settings.loadWithOverviewMode = isDesktop

                    if (webView.settings.userAgentString != desiredUserAgent) {
                        webView.settings.userAgentString = desiredUserAgent
                        val currentUrl = activeTab?.url ?: webView.url ?: Constants.DEFAULT_HOME_URL
                        if (currentUrl.isNotEmpty() && currentUrl != "about:blank") {
                            webView.loadUrl(currentUrl)
                        }
                    } else {
                        val targetUrl = activeTab?.url ?: Constants.DEFAULT_HOME_URL
                        if (webView.url != targetUrl && targetUrl.isNotEmpty() && targetUrl != "about:blank") {
                            webView.loadUrl(targetUrl)
                        }
                    }
                }
            )

            // AI Face Blur / Hide Overlay Canvas Layer
            if (protectionSettings.isMasterEnabled && protectionSettings.detectionMode != DetectionMode.OFF) {
                FaceBlurOverlayCanvas(
                    mappedFaces = mappedOverlayRects,
                    detectionMode = protectionSettings.detectionMode,
                    hideWholeImage = protectionSettings.hideWholeImage,
                    blurRadius = protectionSettings.blurRadius,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Bottom Navigation Bar
        Surface(
            tonalElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { webViewRef?.goBack() },
                    enabled = activeTab?.canGoBack == true
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = if (activeTab?.canGoBack == true) DeenForestPrimary else Color.Gray
                    )
                }

                IconButton(
                    onClick = { webViewRef?.goForward() },
                    enabled = activeTab?.canGoForward == true
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Forward",
                        tint = if (activeTab?.canGoForward == true) DeenForestPrimary else Color.Gray
                    )
                }

                IconButton(onClick = onNavigateHome) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = "Home", tint = DeenForestPrimary)
                }

                // Tab Switcher Button with Counter Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onOpenTabSwitcher() }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Tab, contentDescription = "Tabs", tint = DeenForestPrimary)
                    Text(
                        text = "${tabs.size}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeenForestPrimary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Menu Dropdown Button
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu", tint = DeenForestPrimary)
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(if (isBookmarked) "Remove Bookmark" else "Add Bookmark") },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                showMenu = false
                                viewModel.toggleBookmark()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Bookmarks") },
                            onClick = {
                                showMenu = false
                                onOpenBookmarks()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("History") },
                            onClick = {
                                showMenu = false
                                onOpenHistory()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Downloads") },
                            onClick = {
                                showMenu = false
                                onOpenDownloads()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (activeTab?.isDesktopMode == true) "Mobile View" else "Desktop Site") },
                            leadingIcon = { Icon(imageVector = Icons.Default.DesktopWindows, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.toggleDesktopMode()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("AI Protection Settings") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Security, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onOpenProtectionDashboard()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = {
                                showMenu = false
                                onOpenSettings()
                            }
                        )
                    }
                }
            }
        }
    }
}