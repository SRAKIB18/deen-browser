package com.deenbrowser.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deenbrowser.app.ui.theme.DeenForestPrimary
import com.deenbrowser.app.ui.theme.DeenGoldAccent
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateNext: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "SplashAlpha"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(1500)
        onNavigateNext()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeenForestPrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(alphaAnim)
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Deen Browser Shield",
                tint = DeenGoldAccent,
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Deen Browser",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "On-Device AI Privacy Protection",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}