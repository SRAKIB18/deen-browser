package com.deenbrowser.app.data.models

import android.graphics.Bitmap
import java.util.UUID

data class TabItem(
    val id: String = UUID.randomUUID().toString(),
    var url: String = "about:blank",
    var title: String = "New Tab",
    var favicon: Bitmap? = null,
    var previewScreenshot: Bitmap? = null,
    var isIncognito: Boolean = false,
    var isDesktopMode: Boolean = false,
    var isLoading: Boolean = false,
    var progress: Int = 0,
    var canGoBack: Boolean = false,
    var canGoForward: Boolean = false,
    var isSecure: Boolean = true
)