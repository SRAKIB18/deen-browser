package com.deenbrowser.app

import com.deenbrowser.app.core.utils.UrlUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UrlUtilsTest {

    @Test
    fun testDirectHttpsUrlFormatting() {
        val input = "https://facebook.com"
        val formatted = UrlUtils.formatUrlOrSearch(input)
        assertEquals("https://facebook.com", formatted)
    }

    @Test
    fun testDomainWithoutSchemeFormatting() {
        val input = "youtube.com"
        val formatted = UrlUtils.formatUrlOrSearch(input)
        assertEquals("https://youtube.com", formatted)
    }

    @Test
    fun testSearchQueryFormatting() {
        val input = "islamic privacy browser"
        val formatted = UrlUtils.formatUrlOrSearch(input)
        assertTrue(formatted.startsWith("https://www.google.com/search?q="))
        assertTrue(formatted.contains("islamic"))
    }

    @Test
    fun testDomainExtraction() {
        val url = "https://m.facebook.com/home"
        val domain = UrlUtils.extractDomain(url)
        assertEquals("m.facebook.com", domain)
    }

    @Test
    fun testIsSecureCheck() {
        assertTrue(UrlUtils.isSecure("https://quran.com"))
        assertFalse(UrlUtils.isSecure("http://example.com"))
    }
}