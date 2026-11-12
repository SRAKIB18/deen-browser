package com.deenbrowser.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deenbrowser.app.ui.theme.DeenForestPrimary
import com.deenbrowser.app.ui.theme.DeenGoldAccent
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            title = "Islamic Visual Privacy",
            description = "Deen Browser runs on-device AI face detection to blur or hide facial visual content according to your privacy preference.",
            icon = Icons.Default.Security
        ),
        OnboardingPage(
            title = "Safe Family DNS",
            description = "Built-in integration with Cloudflare and AdGuard Family DNS to protect your connection from malware, trackers, and inappropriate domains.",
            icon = Icons.Default.Dns
        ),
        OnboardingPage(
            title = "Full Modern Browser",
            description = "Enjoy fast web browsing, multiple tabs, incognito mode, bookmarks, downloads, and desktop mode for all your favorite websites.",
            icon = Icons.Default.Public
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Skip Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onFinish) {
                Text("Skip", color = DeenForestPrimary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pager Content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { pageIndex ->
            val page = pages[pageIndex]
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = null,
                    tint = DeenForestPrimary,
                    modifier = Modifier.size(120.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = page.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = page.description,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }

        // Indicators & Buttons
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pages.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .height(8.dp)
                        .width(if (isSelected) 24.dp else 8.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) DeenForestPrimary else MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (pagerState.currentPage < pages.size - 1) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    onFinish()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DeenForestPrimary)
        ) {
            Text(
                text = if (pagerState.currentPage == pages.size - 1) "Get Started" else "Next",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DeenGoldAccent
            )
        }
    }
}