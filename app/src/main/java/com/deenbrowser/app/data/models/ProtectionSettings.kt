package com.deenbrowser.app.data.models

enum class DetectionMode {
    OFF,
    FACE_BLUR,
    STRICT,
    CUSTOM
}

enum class VideoProtectionMode {
    OFF,
    EXPERIMENTAL_BLUR,
    EXPERIMENTAL_HIDE
}

data class WebsiteRule(
    val domain: String,
    val isEnabled: Boolean = true,
    val mode: DetectionMode = DetectionMode.FACE_BLUR,
    val hideWholeImage: Boolean = false
)

data class ProtectionSettings(
    val isMasterEnabled: Boolean = true,
    val detectionMode: DetectionMode = DetectionMode.FACE_BLUR,
    val hideWholeImage: Boolean = false,
    val blurRadius: Float = 25f,
    val sensitivity: Float = 0.5f,
    val videoProtectionMode: VideoProtectionMode = VideoProtectionMode.OFF,
    val videoProcessingIntervalMs: Long = 500L,
    val batterySaverMode: Boolean = true,
    val websiteRules: List<WebsiteRule> = emptyList(),
    val trustedFacesCount: Int = 0
)