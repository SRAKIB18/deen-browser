package com.deenbrowser.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.deenbrowser.app.browser.BrowserScreen
import com.deenbrowser.app.browser.BrowserViewModel
import com.deenbrowser.app.settings.SettingsViewModel
import com.deenbrowser.app.ui.screens.AboutScreen
import com.deenbrowser.app.ui.screens.BookmarksScreen
import com.deenbrowser.app.ui.screens.DnsSettingsScreen
import com.deenbrowser.app.ui.screens.DownloadsScreen
import com.deenbrowser.app.ui.screens.HistoryScreen
import com.deenbrowser.app.ui.screens.HomeScreen
import com.deenbrowser.app.ui.screens.OnboardingScreen
import com.deenbrowser.app.ui.screens.ProtectionDashboardScreen
import com.deenbrowser.app.ui.screens.SplashScreen
import com.deenbrowser.app.ui.screens.TabSwitcherScreen

@Composable
fun DeenNavGraph(
    navController: NavHostController,
    browserViewModel: BrowserViewModel,
    settingsViewModel: SettingsViewModel
) {
    val protectionSettings by settingsViewModel.protectionSettings.collectAsState()
    val dnsSettings by settingsViewModel.dnsSettings.collectAsState()
    val onboardingCompleted by settingsViewModel.onboardingCompleted.collectAsState()
    val tabs by browserViewModel.tabs.collectAsState()
    val activeTabIndex by browserViewModel.activeTabIndex.collectAsState()
    val bookmarks by browserViewModel.bookmarks.collectAsState()
    val history by browserViewModel.history.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateNext = {
                    if (onboardingCompleted) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    settingsViewModel.setOnboardingCompleted(true)
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                protectionSettings = protectionSettings,
                onNavigateToUrl = { url ->
                    browserViewModel.loadUrlOrSearch(url)
                    navController.navigate(Screen.Browser.route)
                },
                onOpenProtectionDashboard = { navController.navigate(Screen.ProtectionDashboard.route) },
                onOpenBookmarks = { navController.navigate(Screen.Bookmarks.route) },
                onOpenHistory = { navController.navigate(Screen.History.route) },
                onOpenDownloads = { navController.navigate(Screen.Downloads.route) },
                onOpenDnsSettings = { navController.navigate(Screen.DnsSettings.route) },
                onOpenSettings = { navController.navigate(Screen.ProtectionDashboard.route) }
            )
        }

        composable(Screen.Browser.route) {
            BrowserScreen(
                viewModel = browserViewModel,
                onNavigateHome = { navController.navigate(Screen.Home.route) },
                onOpenTabSwitcher = { navController.navigate(Screen.TabSwitcher.route) },
                onOpenProtectionDashboard = { navController.navigate(Screen.ProtectionDashboard.route) },
                onOpenBookmarks = { navController.navigate(Screen.Bookmarks.route) },
                onOpenHistory = { navController.navigate(Screen.History.route) },
                onOpenDownloads = { navController.navigate(Screen.Downloads.route) },
                onOpenSettings = { navController.navigate(Screen.ProtectionDashboard.route) }
            )
        }

        composable(Screen.TabSwitcher.route) {
            TabSwitcherScreen(
                tabs = tabs,
                activeTabIndex = activeTabIndex,
                onSelectTab = { index -> browserViewModel.selectTab(index) },
                onCloseTab = { index -> browserViewModel.closeTab(index) },
                onCloseAllTabs = { browserViewModel.closeAllTabs() },
                onNewTab = { isIncognito -> browserViewModel.createTab(isIncognito = isIncognito) },
                onBackToBrowser = { navController.navigate(Screen.Browser.route) }
            )
        }

        composable(Screen.Bookmarks.route) {
            BookmarksScreen(
                bookmarks = bookmarks,
                onSelectBookmark = { url ->
                    browserViewModel.loadUrlOrSearch(url)
                    navController.navigate(Screen.Browser.route)
                },
                onDeleteBookmark = { url -> browserViewModel.repository.let { } },
                onClearAllBookmarks = { browserViewModel.clearBookmarks() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                history = history,
                onSelectHistory = { url ->
                    browserViewModel.loadUrlOrSearch(url)
                    navController.navigate(Screen.Browser.route)
                },
                onDeleteHistory = { id -> browserViewModel.deleteHistoryItem(id) },
                onClearAllHistory = { browserViewModel.clearHistory() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Downloads.route) {
            DownloadsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ProtectionDashboard.route) {
            ProtectionDashboardScreen(
                settings = protectionSettings,
                onUpdateMasterEnabled = { enabled -> settingsViewModel.updateMasterProtection(enabled) },
                onUpdateDetectionMode = { mode -> settingsViewModel.updateDetectionMode(mode) },
                onUpdateHideWholeImage = { hide -> settingsViewModel.updateHideWholeImage(hide) },
                onUpdateBlurRadius = { radius -> settingsViewModel.updateBlurRadius(radius) },
                onUpdateSensitivity = { sensitivity -> settingsViewModel.updateSensitivity(sensitivity) },
                onUpdateVideoProtectionMode = { vMode -> settingsViewModel.updateVideoProtectionMode(vMode) },
                onUpdateBatterySaverMode = { enabled -> settingsViewModel.updateBatterySaverMode(enabled) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.DnsSettings.route) {
            DnsSettingsScreen(
                settings = dnsSettings,
                onUpdateDnsEnabled = { enabled -> settingsViewModel.updateDnsEnabled(enabled) },
                onSelectPreset = { presetName -> settingsViewModel.selectDnsPreset(presetName) },
                onUpdateCustomDot = { hostname -> settingsViewModel.updateCustomDotHostname(hostname) },
                onOpenSystemPrivateDns = { settingsViewModel.openSystemPrivateDnsSettings() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}