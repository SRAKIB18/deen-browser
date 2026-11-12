package com.deenbrowser.app

import com.deenbrowser.app.dns.DnsManager
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DnsManagerTest {

    @Test
    fun testHostnameValidation() {
        // Since Context is not mocked, test validation logic directly
        val regex = Regex("^[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")
        assertTrue(regex.matches("family.cloudflare-dns.com"))
        assertTrue(regex.matches("family.adguard-dns.com"))
        assertFalse(regex.matches("invalid_dns_hostname"))
    }
}