package com.deenbrowser.app.core.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Browser : Screen("browser")
    object TabSwitcher : Screen("tab_switcher")
    object Bookmarks : Screen("bookmarks")
    object History : Screen("history")
    object Downloads : Screen("downloads")
    object ProtectionDashboard : Screen("protection_dashboard")
    object DnsSettings : Screen("dns_settings")
    object About : Screen("about")
}