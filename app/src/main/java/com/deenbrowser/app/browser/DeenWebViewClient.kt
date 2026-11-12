package com.deenbrowser.app.browser

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class DeenWebViewClient(
    private val context: Context,
    private val isDesktopMode: () -> Boolean = { false },
    private val onPageStarted: (String) -> Unit,
    private val onPageFinished: (String, String?) -> Unit,
    private val onUrlChanged: (String) -> Unit
) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val url = request?.url?.toString() ?: return false

        // Handle external protocols (mailto:, tel:, intent:, whatsapp:, etc.)
        if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("about:")) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        onUrlChanged(url)
        return false
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        url?.let {
            onPageStarted(it)
            onUrlChanged(it)
        }
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        url?.let { finishedUrl ->
            val title = view?.title
            onPageFinished(finishedUrl, title)
            injectImageObserverScript(view)

            if (isDesktopMode()) {
                val jsDesktopViewport = """
                    (function() {
                        var meta = document.querySelector('meta[name="viewport"]');
                        if (meta) {
                            meta.setAttribute('content', 'width=1280, initial-scale=0.75');
                        } else {
                            var m = document.createElement('meta');
                            m.name = 'viewport';
                            m.content = 'width=1280, initial-scale=0.75';
                            document.getElementsByTagName('head')[0].appendChild(m);
                        }
                    })();
                """.trimIndent()
                view?.evaluateJavascript(jsDesktopViewport, null)
            }
        }
    }

    /**
     * Injects JS to monitor DOM image elements, lazy loaded images, and scroll position.
     */
    private fun injectImageObserverScript(webView: WebView?) {
        val jsScript = """
            (function() {
                if (window.deenObserverInjected) return;
                window.deenObserverInjected = true;
                
                function scanImages() {
                    const images = document.getElementsByTagName('img');
                    const imgData = [];
                    for (let i = 0; i < images.length; i++) {
                        const img = images[i];
                        const rect = img.getBoundingClientRect();
                        if (rect.width > 20 && rect.height > 20 && img.src) {
                            imgData.push({
                                src: img.src,
                                left: rect.left,
                                top: rect.top,
                                width: rect.width,
                                height: rect.height
                            });
                        }
                    }
                    if (window.DeenJSBridge && window.DeenJSBridge.onImagesDiscovered) {
                        window.DeenJSBridge.onImagesDiscovered(JSON.stringify(imgData));
                    }
                }
                
                window.addEventListener('scroll', scanImages);
                window.addEventListener('resize', scanImages);
                
                const observer = new MutationObserver(function(mutations) {
                    scanImages();
                });
                observer.observe(document.body, { childList: true, subtree: true, attributes: true, attributeFilter: ['src'] });
                
                scanImages();
            })();
        """.trimIndent()

        webView?.evaluateJavascript(jsScript, null)
    }
}