package com.deenbrowser.app.data.models

data class DownloadItem(
    val downloadId: Long,
    val fileName: String,
    val url: String,
    val mimeType: String,
    val totalBytes: Long,
    val downloadedBytes: Long,
    val status: Int,
    val filePath: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)