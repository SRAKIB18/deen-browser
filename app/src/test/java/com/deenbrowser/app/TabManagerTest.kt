package com.deenbrowser.app

import com.deenbrowser.app.browser.TabManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TabManagerTest {

    @Test
    fun testTabCreationAndSelection() {
        val tabManager = TabManager()
        assertEquals(1, tabManager.tabs.value.size)

        val newTab = tabManager.createNewTab("https://youtube.com")
        assertEquals(2, tabManager.tabs.value.size)
        assertEquals(1, tabManager.activeTabIndex.value)
        assertEquals("https://youtube.com", tabManager.activeTab?.url)
    }

    @Test
    fun testTabClosing() {
        val tabManager = TabManager()
        tabManager.createNewTab("https://facebook.com")
        assertEquals(2, tabManager.tabs.value.size)

        tabManager.closeTab(1)
        assertEquals(1, tabManager.tabs.value.size)
        assertEquals(0, tabManager.activeTabIndex.value)
    }

    @Test
    fun testCloseAllTabsRecreatesHomeTab() {
        val tabManager = TabManager()
        tabManager.createNewTab("https://instagram.com")
        tabManager.createNewTab("https://bbc.com")
        assertEquals(3, tabManager.tabs.value.size)

        tabManager.closeAllTabs()
        assertEquals(1, tabManager.tabs.value.size)
        assertNotNull(tabManager.activeTab)
    }
}