package com.deenbrowser.app.core.utils

import android.webkit.URLUtil
import java.net.URI
import java.net.URLEncoder

object UrlUtils {

    fun formatUrlOrSearch(input: String): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return Constants.DEFAULT_HOME_URL

        if (URLUtil.isValidUrl(trimmed)) {
            return trimmed
        }

        if (trimmed.startsWith("http://") || trimmed.startsWith("https://") || trimmed.startsWith("about:")) {
            return trimmed
        }

        val hasDot = trimmed.contains(".") && !trimmed.contains(" ")
        if (hasDot) {
            return "https://$trimmed"
        }

        // Otherwise Google Search
        val encoded = try {
            URLEncoder.encode(trimmed, "UTF-8")
        } catch (e: Exception) {
            trimmed
        }
        return "${Constants.GOOGLE_SEARCH_URL}$encoded"
    }

    fun extractDomain(url: String): String {
        return try {
            val uri = URI(url)
            val domain = uri.host ?: url
            if (domain.startsWith("www.")) domain.substring(4) else domain
        } catch (e: Exception) {
            url
        }
    }

    fun isSecure(url: String): Boolean {
        return url.startsWith("https://")
    }
}