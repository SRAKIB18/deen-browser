package com.deenbrowser.app.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.deenbrowser.app.data.models.DetectionMode
import com.deenbrowser.app.data.models.DnsSettings
import com.deenbrowser.app.data.models.ProtectionSettings
import com.deenbrowser.app.data.models.VideoProtectionMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "deen_browser_preferences")

class UserPreferencesRepository(private val context: Context) {

    private object PreferenceKeys {
        val MASTER_PROTECTION_ENABLED = booleanPreferencesKey("master_protection_enabled")
        val DETECTION_MODE = stringPreferencesKey("detection_mode")
        val HIDE_WHOLE_IMAGE = booleanPreferencesKey("hide_whole_image")
        val BLUR_RADIUS = floatPreferencesKey("blur_radius")
        val SENSITIVITY = floatPreferencesKey("sensitivity")
        val VIDEO_PROTECTION_MODE = stringPreferencesKey("video_protection_mode")
        val VIDEO_INTERVAL_MS = longPreferencesKey("video_interval_ms")
        val BATTERY_SAVER_MODE = booleanPreferencesKey("battery_saver_mode")

        val DNS_ENABLED = booleanPreferencesKey("dns_enabled")
        val DNS_PRESET = stringPreferencesKey("dns_preset")
        val CUSTOM_DOT_HOSTNAME = stringPreferencesKey("custom_dot_hostname")

        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    val protectionSettingsFlow: Flow<ProtectionSettings> = context.dataStore.data.map { prefs ->
        ProtectionSettings(
            isMasterEnabled = prefs[PreferenceKeys.MASTER_PROTECTION_ENABLED] ?: true,
            detectionMode = try {
                DetectionMode.valueOf(prefs[PreferenceKeys.DETECTION_MODE] ?: DetectionMode.FACE_BLUR.name)
            } catch (e: Exception) {
                DetectionMode.FACE_BLUR
            },
            hideWholeImage = prefs[PreferenceKeys.HIDE_WHOLE_IMAGE] ?: false,
            blurRadius = prefs[PreferenceKeys.BLUR_RADIUS] ?: 25f,
            sensitivity = prefs[PreferenceKeys.SENSITIVITY] ?: 0.5f,
            videoProtectionMode = try {
                VideoProtectionMode.valueOf(prefs[PreferenceKeys.VIDEO_PROTECTION_MODE] ?: VideoProtectionMode.OFF.name)
            } catch (e: Exception) {
                VideoProtectionMode.OFF
            },
            videoProcessingIntervalMs = prefs[PreferenceKeys.VIDEO_INTERVAL_MS] ?: 500L,
            batterySaverMode = prefs[PreferenceKeys.BATTERY_SAVER_MODE] ?: true
        )
    }

    val dnsSettingsFlow: Flow<DnsSettings> = context.dataStore.data.map { prefs ->
        DnsSettings(
            isEnabled = prefs[PreferenceKeys.DNS_ENABLED] ?: true,
            selectedPresetName = prefs[PreferenceKeys.DNS_PRESET] ?: "Cloudflare Family",
            customDotHostname = prefs[PreferenceKeys.CUSTOM_DOT_HOSTNAME] ?: "family.cloudflare-dns.com"
        )
    }

    val onboardingCompletedFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[PreferenceKeys.ONBOARDING_COMPLETED] ?: false
    }

    suspend fun updateProtectionSettings(settings: ProtectionSettings) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.MASTER_PROTECTION_ENABLED] = settings.isMasterEnabled
            prefs[PreferenceKeys.DETECTION_MODE] = settings.detectionMode.name
            prefs[PreferenceKeys.HIDE_WHOLE_IMAGE] = settings.hideWholeImage
            prefs[PreferenceKeys.BLUR_RADIUS] = settings.blurRadius
            prefs[PreferenceKeys.SENSITIVITY] = settings.sensitivity
            prefs[PreferenceKeys.VIDEO_PROTECTION_MODE] = settings.videoProtectionMode.name
            prefs[PreferenceKeys.VIDEO_INTERVAL_MS] = settings.videoProcessingIntervalMs
            prefs[PreferenceKeys.BATTERY_SAVER_MODE] = settings.batterySaverMode
        }
    }

    suspend fun updateDnsSettings(settings: DnsSettings) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.DNS_ENABLED] = settings.isEnabled
            prefs[PreferenceKeys.DNS_PRESET] = settings.selectedPresetName
            prefs[PreferenceKeys.CUSTOM_DOT_HOSTNAME] = settings.customDotHostname
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.ONBOARDING_COMPLETED] = completed
        }
    }
}