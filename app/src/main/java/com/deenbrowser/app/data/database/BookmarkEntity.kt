package com.deenbrowser.app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.deenbrowser.app.data.models.Bookmark

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val category: String,
    val timestamp: Long
)

fun BookmarkEntity.toModel(): Bookmark = Bookmark(
    id = id,
    title = title,
    url = url,
    category = category,
    timestamp = timestamp
)

fun Bookmark.toEntity(): BookmarkEntity = BookmarkEntity(
    id = id,
    title = title,
    url = url,
    category = category,
    timestamp = timestamp
)