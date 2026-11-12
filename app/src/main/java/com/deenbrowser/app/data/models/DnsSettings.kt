package com.deenbrowser.app.data.models

data class DnsSettings(
    val isEnabled: Boolean = true,
    val selectedPresetName: String = "Cloudflare Family",
    val customDotHostname: String = "family.cloudflare-dns.com",
    val isVpnActive: Boolean = false
)