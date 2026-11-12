package com.deenbrowser.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deenbrowser.app.data.models.TabItem
import com.deenbrowser.app.ui.theme.DeenForestPrimary
import com.deenbrowser.app.ui.theme.DeenGoldAccent

@Composable
fun TabSwitcherScreen(
    tabs: List<TabItem>,
    activeTabIndex: Int,
    onSelectTab: (Int) -> Unit,
    onCloseTab: (Int) -> Unit,
    onCloseAllTabs: () -> Unit,
    onNewTab: (isIncognito: Boolean) -> Unit,
    onBackToBrowser: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackToBrowser) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back to Browser", tint = MaterialTheme.colorScheme.onBackground)
            }

            Text(
                text = "${tabs.size} Open Tabs",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            IconButton(onClick = onCloseAllTabs) {
                Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Close All Tabs", tint = Color.Red)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid of Tabs
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(tabs) { index, tab ->
                val isActive = index == activeTabIndex
                TabCardItem(
                    tab = tab,
                    isActive = isActive,
                    onSelect = {
                        onSelectTab(index)
                        onBackToBrowser()
                    },
                    onClose = { onCloseTab(index) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom Actions: New Tab / Incognito
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    onNewTab(false)
                    onBackToBrowser()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeenForestPrimary)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = DeenGoldAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Text("New Tab", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TabCardItem(
    tab: TabItem,
    isActive: Boolean,
    onSelect: () -> Unit,
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isActive) 2.dp else 0.dp,
                color = if (isActive) DeenForestPrimary else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (tab.isIncognito) Color(0xFF2C2C2C) else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tab.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (tab.isIncognito) Color.White else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Tab",
                        tint = if (tab.isIncognito) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = tab.url,
                fontSize = 11.sp,
                color = if (tab.isIncognito) Color.LightGray else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (tab.isIncognito) {
                Text(
                    text = "Incognito Mode",
                    fontSize = 10.sp,
                    color = DeenGoldAccent,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}