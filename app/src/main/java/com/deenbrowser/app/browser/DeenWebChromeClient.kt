package com.deenbrowser.app.browser

import android.graphics.Bitmap
import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView

class DeenWebChromeClient(
    private val onProgressChanged: (Int) -> Unit,
    private val onTitleReceived: (String) -> Unit,
    private val onFaviconReceived: (Bitmap) -> Unit,
    private val onShowFileChooserCallback: ((ValueCallback<Array<Uri>>?, WebChromeClient.FileChooserParams?) -> Boolean)? = null
) : WebChromeClient() {

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        onProgressChanged(newProgress)
    }

    override fun onReceivedTitle(view: WebView?, title: String?) {
        super.onReceivedTitle(view, title)
        title?.let { onTitleReceived(it) }
    }

    override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
        super.onReceivedIcon(view, icon)
        icon?.let { onFaviconReceived(it) }
    }

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        return onShowFileChooserCallback?.invoke(filePathCallback, fileChooserParams)
            ?: super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
    }
}