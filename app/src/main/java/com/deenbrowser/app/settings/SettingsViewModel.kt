package com.deenbrowser.app.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.deenbrowser.app.data.datastore.UserPreferencesRepository
import com.deenbrowser.app.data.models.DetectionMode
import com.deenbrowser.app.data.models.DnsSettings
import com.deenbrowser.app.data.models.ProtectionSettings
import com.deenbrowser.app.data.models.VideoProtectionMode
import com.deenbrowser.app.dns.DnsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferencesRepository = UserPreferencesRepository(application)
    val dnsManager = DnsManager(application)

    private val _protectionSettings = MutableStateFlow(ProtectionSettings())
    val protectionSettings: StateFlow<ProtectionSettings> = _protectionSettings.asStateFlow()

    private val _dnsSettings = MutableStateFlow(DnsSettings())
    val dnsSettings: StateFlow<DnsSettings> = _dnsSettings.asStateFlow()

    private val _onboardingCompleted = MutableStateFlow(false)
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferencesRepository.protectionSettingsFlow.collectLatest {
                _protectionSettings.value = it
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.dnsSettingsFlow.collectLatest {
                _dnsSettings.value = it
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.onboardingCompletedFlow.collectLatest {
                _onboardingCompleted.value = it
            }
        }
    }

    fun updateMasterProtection(enabled: Boolean) {
        val updated = _protectionSettings.value.copy(isMasterEnabled = enabled)
        saveProtectionSettings(updated)
    }

    fun updateDetectionMode(mode: DetectionMode) {
        val updated = _protectionSettings.value.copy(detectionMode = mode)
        saveProtectionSettings(updated)
    }

    fun updateHideWholeImage(hide: Boolean) {
        val updated = _protectionSettings.value.copy(hideWholeImage = hide)
        saveProtectionSettings(updated)
    }

    fun updateBlurRadius(radius: Float) {
        val updated = _protectionSettings.value.copy(blurRadius = radius)
        saveProtectionSettings(updated)
    }

    fun updateSensitivity(sensitivity: Float) {
        val updated = _protectionSettings.value.copy(sensitivity = sensitivity)
        saveProtectionSettings(updated)
    }

    fun updateVideoProtectionMode(mode: VideoProtectionMode) {
        val updated = _protectionSettings.value.copy(videoProtectionMode = mode)
        saveProtectionSettings(updated)
    }

    fun updateBatterySaverMode(enabled: Boolean) {
        val updated = _protectionSettings.value.copy(batterySaverMode = enabled)
        saveProtectionSettings(updated)
    }

    private fun saveProtectionSettings(settings: ProtectionSettings) {
        viewModelScope.launch {
            userPreferencesRepository.updateProtectionSettings(settings)
        }
    }

    fun updateDnsEnabled(enabled: Boolean) {
        val updated = _dnsSettings.value.copy(isEnabled = enabled)
        saveDnsSettings(updated)
    }

    fun selectDnsPreset(presetName: String) {
        val hostname = dnsManager.getPresetHostname(presetName, _dnsSettings.value.customDotHostname)
        val updated = _dnsSettings.value.copy(
            selectedPresetName = presetName,
            customDotHostname = hostname
        )
        saveDnsSettings(updated)
    }

    fun updateCustomDotHostname(hostname: String) {
        val updated = _dnsSettings.value.copy(customDotHostname = hostname)
        saveDnsSettings(updated)
    }

    private fun saveDnsSettings(settings: DnsSettings) {
        viewModelScope.launch {
            userPreferencesRepository.updateDnsSettings(settings)
        }
    }

    fun setOnboardingCompleted(completed: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setOnboardingCompleted(completed)
        }
    }

    fun openSystemPrivateDnsSettings(): Boolean {
        return dnsManager.openSystemPrivateDnsSettings()
    }
}