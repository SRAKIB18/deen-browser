package com.deenbrowser.app.core.utils

object Constants {
    const val DEFAULT_HOME_URL = "about:blank"
    const val GOOGLE_SEARCH_URL = "https://www.google.com/search?q="

    // Quick Access Shortcuts
    val QUICK_ACCESS_SITES = listOf(
        QuickSite("Facebook", "https://m.facebook.com", "social"),
        QuickSite("YouTube", "https://m.youtube.com", "video"),
        QuickSite("Instagram", "https://www.instagram.com", "social"),
        QuickSite("X (Twitter)", "https://mobile.twitter.com", "social"),
        QuickSite("BBC News", "https://www.bbc.com/news", "news"),
        QuickSite("Wikipedia", "https://en.wikipedia.org", "education"),
        QuickSite("Amazon", "https://www.amazon.com", "shopping"),
        QuickSite("Quran.com", "https://quran.com", "islamic")
    )

    // User Agents
    const val MOBILE_USER_AGENT = "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"
    const val DESKTOP_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"

    // Safe DNS Presets
    val SAFE_DNS_PRESETS = listOf(
        DnsPreset("Cloudflare Family", "family.cloudflare-dns.com", "Blocks malware and adult content", "1.1.1.3"),
        DnsPreset("AdGuard Family", "family.adguard-dns.com", "Blocks ads, trackers, and adult sites", "94.140.14.15"),
        DnsPreset("CleanBrowsing Family", "family-filter-dns.cleanbrowsing.org", "Strict safe search and adult filter", "185.228.168.168"),
        DnsPreset("Custom DoT", "", "User-configured DNS-over-TLS hostname", "")
    )
}

data class QuickSite(
    val title: String,
    val url: String,
    val category: String
)

data class DnsPreset(
    val name: String,
    val hostname: String,
    val description: String,
    val ip: String
)