package com.deenbrowser.app.protection

import android.graphics.Rect
import android.util.LruCache

class DetectionCache {
    // Cache face bounding boxes relative to normalized bitmap coordinates (0.0 to 1.0)
    private val memoryCache = LruCache<String, List<RectFNorm>>(100)

    fun get(url: String): List<RectFNorm>? {
        return memoryCache.get(url)
    }

    fun put(url: String, normalizedFaces: List<RectFNorm>) {
        memoryCache.put(url, normalizedFaces)
    }

    fun clear() {
        memoryCache.evictAll()
    }
}

data class RectFNorm(
    val leftNorm: Float,
    val topNorm: Float,
    val rightNorm: Float,
    val bottomNorm: Float
)