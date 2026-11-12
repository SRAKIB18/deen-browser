package com.deenbrowser.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import com.deenbrowser.app.core.utils.Constants
import com.deenbrowser.app.core.utils.DnsPreset
import com.deenbrowser.app.data.models.DnsSettings
import com.deenbrowser.app.ui.theme.DeenForestPrimary

@Composable
fun DnsSettingsScreen(
    settings: DnsSettings,
    onUpdateDnsEnabled: (Boolean) -> Unit,
    onSelectPreset: (String) -> Unit,
    onUpdateCustomDot: (String) -> Unit,
    onOpenSystemPrivateDns: () -> Boolean,
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Safe & Private DNS",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Enable Switch Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = DeenForestPrimary, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Enable Safe DNS Presets", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Blocks malware and adult domains", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Switch(
                    checked = settings.isEnabled,
                    onCheckedChange = onUpdateDnsEnabled,
                    colors = SwitchDefaults.colors(checkedTrackColor = DeenForestPrimary)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Safe DNS Providers", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(12.dp))

        Constants.SAFE_DNS_PRESETS.forEach { preset ->
            DnsPresetCard(
                preset = preset,
                isSelected = settings.selectedPresetName == preset.name,
                onSelect = { onSelectPreset(preset.name) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (settings.selectedPresetName == "Custom DoT") {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = settings.customDotHostname,
                onValueChange = onUpdateCustomDot,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Custom DNS-over-TLS Hostname") },
                placeholder = { Text("e.g. family.cloudflare-dns.com") },
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // System Private DNS Button
        Button(
            onClick = { onOpenSystemPrivateDns() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DeenForestPrimary)
        ) {
            Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Open Android System Private DNS Settings", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DnsPresetCard(
    preset: DnsPreset,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
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
                    text = preset.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) DeenForestPrimary else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = preset.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (preset.hostname.isNotEmpty()) {
                    Text(
                        text = "DoT Hostname: ${preset.hostname}",
                        fontSize = 11.sp,
                        color = DeenForestPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}