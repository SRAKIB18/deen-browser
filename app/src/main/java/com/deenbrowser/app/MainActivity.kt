package com.deenbrowser.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.deenbrowser.app.browser.BrowserViewModel
import com.deenbrowser.app.core.navigation.DeenNavGraph
import com.deenbrowser.app.settings.SettingsViewModel
import com.deenbrowser.app.ui.theme.DeenBrowserTheme

class MainActivity : ComponentActivity() {

    private val browserViewModel: BrowserViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DeenBrowserTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    DeenNavGraph(
                        navController = navController,
                        browserViewModel = browserViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }
}