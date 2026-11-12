package com.deenbrowser.app.protection

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.webkit.WebView
import com.deenbrowser.app.data.models.DetectionMode
import com.deenbrowser.app.data.models.ProtectionSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FaceProtectionEngine(
    private val scope: CoroutineScope
) {
    private val faceDetector = FaceDetectionManager()

    private val _detectedFaceRects = MutableStateFlow<List<Rect>>(emptyList())
    val detectedFaceRects: StateFlow<List<Rect>> = _detectedFaceRects.asStateFlow()

    private val _bitmapWidth = MutableStateFlow(1)
    val bitmapWidth: StateFlow<Int> = _bitmapWidth.asStateFlow()

    private val _bitmapHeight = MutableStateFlow(1)
    val bitmapHeight: StateFlow<Int> = _bitmapHeight.asStateFlow()

    private var detectionJob: Job? = null
    private var isProcessing = false

    fun startProtectionLoop(
        webViewGetter: () -> WebView?,
        settingsGetter: () -> ProtectionSettings
    ) {
        detectionJob?.cancel()
        detectionJob = scope.launch(Dispatchers.Default) {
            while (true) {
                val settings = settingsGetter()
                if (settings.isMasterEnabled && settings.detectionMode != DetectionMode.OFF) {
                    processWebViewFrame(webViewGetter(), settings)
                } else {
                    if (_detectedFaceRects.value.isNotEmpty()) {
                        _detectedFaceRects.value = emptyList()
                    }
                }

                val interval = if (settings.batterySaverMode) 800L else 400L
                delay(interval)
            }
        }
    }

    fun triggerFrameScan(webViewGetter: () -> WebView?, settingsGetter: () -> ProtectionSettings) {
        scope.launch(Dispatchers.Default) {
            val settings = settingsGetter()
            if (settings.isMasterEnabled && settings.detectionMode != DetectionMode.OFF) {
                processWebViewFrame(webViewGetter(), settings)
            }
        }
    }

    private suspend fun processWebViewFrame(webView: WebView?, settings: ProtectionSettings) {
        if (webView == null || isProcessing) return
        if (webView.width <= 0 || webView.height <= 0) return

        isProcessing = true
        try {
            val bitmap = captureWebViewBitmap(webView)
            if (bitmap != null) {
                _bitmapWidth.value = bitmap.width
                _bitmapHeight.value = bitmap.height

                val faces = faceDetector.detectFaces(bitmap)
                _detectedFaceRects.value = faces
                bitmap.recycle()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isProcessing = false
        }
    }

    private suspend fun captureWebViewBitmap(webView: WebView): Bitmap? = withContext(Dispatchers.Main) {
        return@withContext try {
            val width = (webView.width / 2).coerceAtLeast(1)
            val height = (webView.height / 2).coerceAtLeast(1)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.scale(0.5f, 0.5f)
            webView.draw(canvas)
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    fun stopProtection() {
        detectionJob?.cancel()
        _detectedFaceRects.value = emptyList()
    }

    fun close() {
        stopProtection()
        faceDetector.close()
    }
}