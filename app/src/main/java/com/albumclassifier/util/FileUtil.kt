package com.albumclassifier.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.util.UUID

object FileUtil {

    private const val MEDIA_DIR = "media"

    /**
     * 将外部 URI 的文件复制到应用私有目录
     */
    fun copyToPrivateDir(context: Context, sourceUri: Uri): Uri? {
        return try {
            val mediaDir = File(context.filesDir, MEDIA_DIR).apply { mkdirs() }
            val ext = getExtension(context, sourceUri)
            val fileName = "${UUID.randomUUID()}$ext"
            val destFile = File(mediaDir, fileName)

            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            Uri.fromFile(destFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 获取文件名
     */
    fun getFileName(context: Context, uri: Uri): String {
        var name = "unknown"
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex >= 0) {
                name = cursor.getString(nameIndex)
            }
        }
        return name
    }

    /**
     * 获取文件大小
     */
    fun getFileSize(context: Context, uri: Uri): Long {
        var size = 0L
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (cursor.moveToFirst() && sizeIndex >= 0) {
                size = cursor.getLong(sizeIndex)
            }
        }
        return size
    }

    /**
     * 获取文件扩展名
     */
    private fun getExtension(context: Context, uri: Uri): String {
        val mimeType = context.contentResolver.getType(uri)
        return when {
            mimeType?.contains("jpeg") == true -> ".jpg"
            mimeType?.contains("png") == true -> ".png"
            mimeType?.contains("webp") == true -> ".webp"
            mimeType?.contains("mp4") == true -> ".mp4"
            mimeType?.contains("video") == true -> ".mp4"
            else -> ".jpg"
        }
    }

    /**
     * 删除文件
     */
    fun deleteFile(context: Context, uri: Uri) {
        try {
            val file = File(uri.path ?: return)
            if (file.exists()) file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
