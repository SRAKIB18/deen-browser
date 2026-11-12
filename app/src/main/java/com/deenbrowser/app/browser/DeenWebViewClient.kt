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
     * Injects JS to insert global CSS blur rules, monitor DOM image elements, lazy loaded images, and report to Native.
     */
    private fun injectImageObserverScript(webView: WebView?) {
        val jsScript = """
            (function() {
                // 1. Inject Global CSS Stylesheet for In-DOM Blurring
                if (!document.getElementById('deen-blur-stylesheet')) {
                    var style = document.createElement('style');
                    style.id = 'deen-blur-stylesheet';
                    style.innerHTML = 'img[data-deen-blur="true"] { filter: blur(28px) !important; -webkit-filter: blur(28px) !important; transition: filter 0.15s ease-in-out !important; } ' +
                                      'img[data-deen-strict="true"] { filter: brightness(0) blur(6px) !important; -webkit-filter: brightness(0) blur(6px) !important; transition: filter 0.15s ease-in-out !important; }';
                    (document.head || document.documentElement).appendChild(style);
                }

                // 2. Function to mark DOM elements for blurring
                window.DeenApplyBlurToImage = function(srcUrl, mode, blurRadius) {
                    try {
                        var imgs = document.querySelectorAll('img');
                        for (var i = 0; i < imgs.length; i++) {
                            var img = imgs[i];
                            var currentSrc = img.src || img.getAttribute('src') || img.getAttribute('data-src') || '';
                            if (currentSrc && (currentSrc === srcUrl || currentSrc.indexOf(srcUrl) !== -1 || srcUrl.indexOf(currentSrc) !== -1)) {
                                if (mode === 'STRICT') {
                                    img.setAttribute('data-deen-strict', 'true');
                                    img.removeAttribute('data-deen-blur');
                                } else {
                                    img.setAttribute('data-deen-blur', 'true');
                                    img.removeAttribute('data-deen-strict');
                                }
                            }
                        }
                    } catch(e) {}
                };

                if (window.deenObserverInjected) return;
                window.deenObserverInjected = true;
                
                // 3. Scan DOM for visible images
                function scanImages() {
                    const images = document.getElementsByTagName('img');
                    const imgData = [];
                    for (let i = 0; i < images.length; i++) {
                        const img = images[i];
                        const rect = img.getBoundingClientRect();
                        const src = img.src || img.getAttribute('src') || img.getAttribute('data-src');
                        if (rect.width > 20 && rect.height > 20 && src) {
                            imgData.push({
                                src: src,
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
                
                window.addEventListener('scroll', scanImages, { passive: true });
                window.addEventListener('resize', scanImages, { passive: true });
                
                const observer = new MutationObserver(function(mutations) {
                    scanImages();
                });
                observer.observe(document.body || document.documentElement, { childList: true, subtree: true, attributes: true, attributeFilter: ['src', 'data-src', 'srcset'] });
                
                scanImages();
            })();
        """.trimIndent()

        webView?.evaluateJavascript(jsScript, null)
    }
}