package com.deenbrowser.app.data.models

data class Bookmark(
    val id: Long = 0,
    val title: String,
    val url: String,
    val category: String = "General",
    val timestamp: Long = System.currentTimeMillis()
)