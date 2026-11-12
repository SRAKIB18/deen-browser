package com.deenbrowser.app.dns

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.deenbrowser.app.core.utils.Constants
import com.deenbrowser.app.data.models.DnsSettings

class DnsManager(private val context: Context) {

    fun getPresetHostname(presetName: String, customInput: String): String {
        val preset = Constants.SAFE_DNS_PRESETS.find { it.name == presetName }
        return if (preset != null && preset.hostname.isNotEmpty()) {
            preset.hostname
        } else {
            customInput.ifBlank { "family.cloudflare-dns.com" }
        }
    }

    fun openSystemPrivateDnsSettings(): Boolean {
        return try {
            val intent = Intent("android.settings.PRIVATE_DNS_SETTINGS")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            try {
                val fallbackIntent = Intent(Settings.ACTION_SETTINGS)
                fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(fallbackIntent)
                true
            } catch (ex: Exception) {
                false
            }
        }
    }

    fun validateHostname(hostname: String): Boolean {
        if (hostname.isBlank()) return false
        val regex = Regex("^[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")
        return regex.matches(hostname)
    }
}