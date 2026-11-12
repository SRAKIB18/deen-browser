package com.deenbrowser.app.browser

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.URLUtil
import android.widget.Toast

class DownloadManagerHelper(private val context: Context) {

    fun downloadFile(
        url: String,
        userAgent: String?,
        contentDisposition: String?,
        mimeType: String?
    ): Long {
        return try {
            val fileName = URLUtil.guessFileName(url, contentDisposition, mimeType)
            val request = DownloadManager.Request(Uri.parse(url)).apply {
                setMimeType(mimeType)
                addRequestHeader("User-Agent", userAgent)
                setDescription("Downloading file...")
                setTitle(fileName)
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            }

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadId = downloadManager.enqueue(request)
            Toast.makeText(context, "Download started: $fileName", Toast.LENGTH_SHORT).show()
            downloadId
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Download failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            -1L
        }
    }
}