package com.deenbrowser.app.browser

import android.content.Context
import com.deenbrowser.app.data.database.AppDatabase
import com.deenbrowser.app.data.database.toEntity
import com.deenbrowser.app.data.database.toModel
import com.deenbrowser.app.data.datastore.UserPreferencesRepository
import com.deenbrowser.app.data.models.Bookmark
import com.deenbrowser.app.data.models.HistoryItem
import com.deenbrowser.app.data.models.ProtectionSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BrowserRepository(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val bookmarkDao = db.bookmarkDao()
    private val historyDao = db.historyDao()
    val preferencesRepository = UserPreferencesRepository(context)

    val allBookmarks: Flow<List<Bookmark>> = bookmarkDao.getAllBookmarks().map { list ->
        list.map { it.toModel() }
    }

    val allHistory: Flow<List<HistoryItem>> = historyDao.getAllHistory().map { list ->
        list.map { it.toModel() }
    }

    val protectionSettings: Flow<ProtectionSettings> = preferencesRepository.protectionSettingsFlow

    suspend fun isBookmarked(url: String): Boolean {
        return bookmarkDao.getBookmarkByUrl(url) != null
    }

    suspend fun addBookmark(title: String, url: String, category: String = "General") {
        val existing = bookmarkDao.getBookmarkByUrl(url)
        if (existing == null) {
            val bookmark = Bookmark(title = title.ifBlank { url }, url = url, category = category)
            bookmarkDao.insertBookmark(bookmark.toEntity())
        }
    }

    suspend fun removeBookmark(url: String) {
        val existing = bookmarkDao.getBookmarkByUrl(url)
        if (existing != null) {
            bookmarkDao.deleteBookmark(existing)
        }
    }

    suspend fun addHistoryItem(title: String, url: String) {
        if (url.startsWith("about:") || url.isBlank()) return
        val item = HistoryItem(title = title.ifBlank { url }, url = url)
        historyDao.insertHistory(item.toEntity())
    }

    suspend fun deleteHistoryItem(id: Long) {
        historyDao.deleteHistoryById(id)
    }

    suspend fun clearHistory() {
        historyDao.clearAllHistory()
    }

    suspend fun clearBookmarks() {
        bookmarkDao.clearAllBookmarks()
    }
}