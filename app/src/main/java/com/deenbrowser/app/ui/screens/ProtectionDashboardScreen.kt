package com.deenbrowser.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deenbrowser.app.data.models.DetectionMode
import com.deenbrowser.app.data.models.ProtectionSettings
import com.deenbrowser.app.data.models.VideoProtectionMode
import com.deenbrowser.app.ui.theme.DeenForestPrimary
import com.deenbrowser.app.ui.theme.DeenGoldAccent
import com.deenbrowser.app.ui.theme.DeenMintAccent

@Composable
fun ProtectionDashboardScreen(
    settings: ProtectionSettings,
    onUpdateMasterEnabled: (Boolean) -> Unit,
    onUpdateDetectionMode: (DetectionMode) -> Unit,
    onUpdateHideWholeImage: (Boolean) -> Unit,
    onUpdateBlurRadius: (Float) -> Unit,
    onUpdateSensitivity: (Float) -> Unit,
    onUpdateVideoProtectionMode: (VideoProtectionMode) -> Unit,
    onUpdateBatterySaverMode: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Top Header Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Islamic AI Protection Dashboard",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Master Switch Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DeenForestPrimary)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = DeenGoldAccent, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Master AI Protection", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(
                            if (settings.isMasterEnabled) "On-Device Detection Active" else "Protection Disabled",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Switch(
                    checked = settings.isMasterEnabled,
                    onCheckedChange = onUpdateMasterEnabled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = DeenForestPrimary,
                        checkedTrackColor = DeenMintAccent
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Detection Modes", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(12.dp))

        // Mode Cards
        DetectionMode.values().forEach { mode ->
            ModeSelectionCard(
                mode = mode,
                isSelected = settings.detectionMode == mode,
                onSelect = { onUpdateDetectionMode(mode) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Filtering Parameters", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(12.dp))

        // Whole Image Hide Toggle
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Hide Whole Image", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Hide the full image element if a face is detected", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = settings.hideWholeImage,
                    onCheckedChange = onUpdateHideWholeImage,
                    colors = SwitchDefaults.colors(checkedTrackColor = DeenForestPrimary)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Blur Strength Slider
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Blur Radius / Intensity", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("${settings.blurRadius.toInt()} px", fontSize = 14.sp, color = DeenForestPrimary, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = settings.blurRadius,
                    onValueChange = onUpdateBlurRadius,
                    valueRange = 10f..50f,
                    colors = SliderDefaults.colors(thumbColor = DeenForestPrimary, activeTrackColor = DeenForestPrimary)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sensitivity Slider
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Face Detection Sensitivity", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("${(settings.sensitivity * 100).toInt()}%", fontSize = 14.sp, color = DeenForestPrimary, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = settings.sensitivity,
                    onValueChange = onUpdateSensitivity,
                    valueRange = 0.1f..0.9f,
                    colors = SliderDefaults.colors(thumbColor = DeenForestPrimary, activeTrackColor = DeenForestPrimary)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Experimental Video Protection", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(12.dp))

        // Video Mode Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Video Filtering Mode", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                VideoProtectionMode.values().forEach { vMode ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onUpdateVideoProtectionMode(vMode) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = settings.videoProtectionMode == vMode,
                            onClick = { onUpdateVideoProtectionMode(vMode) },
                            colors = RadioButtonDefaults.colors(selectedColor = DeenForestPrimary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(vMode.name.replace("_", " "), fontSize = 14.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Battery Saver Toggle
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Battery Saver Mode", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("Throttle frame detection to conserve battery", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = settings.batterySaverMode,
                    onCheckedChange = onUpdateBatterySaverMode,
                    colors = SwitchDefaults.colors(checkedTrackColor = DeenForestPrimary)
                )
            }
        }
    }
}

@Composable
fun ModeSelectionCard(
    mode: DetectionMode,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val description = when (mode) {
        DetectionMode.OFF -> "No face detection. Standard web browsing."
        DetectionMode.FACE_BLUR -> "Detect visible faces and apply smooth blur overlays on face regions."
        DetectionMode.STRICT -> "Opaque hide detected face regions or hide the containing image element."
        DetectionMode.CUSTOM -> "Per-website customized rules and sensitivity."
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) DeenForestPrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(selectedColor = DeenForestPrimary)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mode.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) DeenForestPrimary else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}