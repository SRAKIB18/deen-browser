package com.deenbrowser.app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.deenbrowser.app.data.models.HistoryItem

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val timestamp: Long
)

fun HistoryEntity.toModel(): HistoryItem = HistoryItem(
    id = id,
    title = title,
    url = url,
    timestamp = timestamp
)

fun HistoryItem.toEntity(): HistoryEntity = HistoryEntity(
    id = id,
    title = title,
    url = url,
    timestamp = timestamp
)