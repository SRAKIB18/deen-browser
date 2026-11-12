package com.deenbrowser.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deenbrowser.app.core.utils.Constants
import com.deenbrowser.app.core.utils.QuickSite
import com.deenbrowser.app.data.models.ProtectionSettings
import com.deenbrowser.app.ui.theme.DeenForestPrimary
import com.deenbrowser.app.ui.theme.DeenGoldAccent
import com.deenbrowser.app.ui.theme.DeenMintAccent

@Composable
fun HomeScreen(
    protectionSettings: ProtectionSettings,
    onNavigateToUrl: (String) -> Unit,
    onOpenProtectionDashboard: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenDownloads: () -> Unit,
    onOpenDnsSettings: () -> Unit,
    onOpenSettings: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = DeenForestPrimary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Deen Browser",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            IconButton(onClick = onOpenSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Protection Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenProtectionDashboard() },
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
                Column {
                    Text(
                        text = if (protectionSettings.isMasterEnabled) "AI Protection Active" else "Protection Disabled",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Mode: ${protectionSettings.detectionMode.name} • On-Device AI",
                        fontSize = 13.sp,
                        color = DeenGoldAccent
                    )
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (protectionSettings.isMasterEnabled) DeenMintAccent else Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = DeenForestPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Search Bar
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search Google or type URL") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = DeenForestPrimary)
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = DeenForestPrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                if (searchText.isNotBlank()) {
                    onNavigateToUrl(searchText)
                }
            })
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Quick Access",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Access Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(Constants.QUICK_ACCESS_SITES) { site ->
                QuickSiteItem(site = site, onClick = { onNavigateToUrl(site.url) })
            }
        }

        // Bottom Action Bar (Bookmarks, History, Downloads, DNS)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionIconItem(icon = Icons.Default.Bookmark, label = "Bookmarks", onClick = onOpenBookmarks)
            ActionIconItem(icon = Icons.Default.History, label = "History", onClick = onOpenHistory)
            ActionIconItem(icon = Icons.Default.Download, label = "Downloads", onClick = onOpenDownloads)
            ActionIconItem(icon = Icons.Default.Lock, label = "Safe DNS", onClick = onOpenDnsSettings)
        }
    }
}

@Composable
fun QuickSiteItem(site: QuickSite, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(DeenForestPrimary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = site.title,
                tint = DeenForestPrimary,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = site.title,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ActionIconItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = DeenForestPrimary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}