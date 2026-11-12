package com.deenbrowser.app.protection

import android.graphics.BitmapFactory
import android.graphics.RectF
import com.deenbrowser.app.data.models.DiscoveredImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL

class ImageProcessingManager(
    private val scope: CoroutineScope,
    var onFacesFoundInImage: ((src: String, hasFaces: Boolean) -> Unit)? = null
) {
    private val faceDetector = FaceDetectionManager()
    private val cache = DetectionCache()

    private val _mappedOverlayRects = MutableStateFlow<List<RectF>>(emptyList())
    val mappedOverlayRects: StateFlow<List<RectF>> = _mappedOverlayRects.asStateFlow()

    fun processDiscoveredImagesJson(jsonString: String) {
        scope.launch(Dispatchers.Default) {
            val images = parseDiscoveredImages(jsonString)
            val allMappedFaces = mutableListOf<RectF>()

            for (img in images) {
                if (img.width < 30 || img.height < 30 || img.src.isBlank()) continue

                val cachedNormFaces = cache.get(img.src)
                val normFaces = if (cachedNormFaces != null) {
                    cachedNormFaces
                } else {
                    val fetchedNorm = fetchAndDetectFaces(img.src)
                    if (fetchedNorm != null) {
                        cache.put(img.src, fetchedNorm)
                        fetchedNorm
                    } else {
                        emptyList()
                    }
                }

                if (normFaces.isNotEmpty()) {
                    onFacesFoundInImage?.invoke(img.src, true)
                }

                for (normFace in normFaces) {
                    val screenLeft = img.left + (normFace.leftNorm * img.width)
                    val screenTop = img.top + (normFace.topNorm * img.height)
                    val screenRight = img.left + (normFace.rightNorm * img.width)
                    val screenBottom = img.top + (normFace.bottomNorm * img.height)

                    allMappedFaces.add(RectF(screenLeft, screenTop, screenRight, screenBottom))
                }
            }

            _mappedOverlayRects.value = allMappedFaces
        }
    }

    private suspend fun fetchAndDetectFaces(srcUrl: String): List<RectFNorm>? = withContext(Dispatchers.IO) {
        return@withContext try {
            if (srcUrl.startsWith("data:image")) {
                // Base64 encoded image
                val base64Data = srcUrl.substringAfter("base64,")
                val decodedBytes = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size) ?: return@withContext null
                val faces = faceDetector.detectFaces(bitmap)
                val w = bitmap.width.toFloat()
                val h = bitmap.height.toFloat()
                val normalized = faces.map { rect ->
                    RectFNorm(
                        leftNorm = rect.left / w,
                        topNorm = rect.top / h,
                        rightNorm = rect.right / w,
                        bottomNorm = rect.bottom / h
                    )
                }
                bitmap.recycle()
                normalized
            } else if (srcUrl.startsWith("http://") || srcUrl.startsWith("https://")) {
                val url = URL(srcUrl)
                val connection = url.openConnection()
                connection.connectTimeout = 3000
                connection.readTimeout = 3000
                val inputStream = connection.getInputStream()
                val bitmap = BitmapFactory.decodeStream(inputStream) ?: return@withContext null
                inputStream.close()

                val faces = faceDetector.detectFaces(bitmap)
                val w = bitmap.width.toFloat()
                val h = bitmap.height.toFloat()
                val normalized = faces.map { rect ->
                    RectFNorm(
                        leftNorm = rect.left / w,
                        topNorm = rect.top / h,
                        rightNorm = rect.right / w,
                        bottomNorm = rect.bottom / h
                    )
                }
                bitmap.recycle()
                normalized
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun parseDiscoveredImages(jsonString: String): List<DiscoveredImage> {
        val list = mutableListOf<DiscoveredImage>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    DiscoveredImage(
                        src = obj.optString("src"),
                        left = obj.optDouble("left").toFloat(),
                        top = obj.optDouble("top").toFloat(),
                        width = obj.optDouble("width").toFloat(),
                        height = obj.optDouble("height").toFloat()
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun clearCache() {
        cache.clear()
        _mappedOverlayRects.value = emptyList()
    }

    fun close() {
        clearCache()
        faceDetector.close()
    }
}