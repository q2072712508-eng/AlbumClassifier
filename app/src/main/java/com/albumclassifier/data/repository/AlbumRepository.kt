package com.albumclassifier.data.repository

import android.content.Context
import android.net.Uri
import com.albumclassifier.data.db.AppDatabase
import com.albumclassifier.data.db.entity.Album
import com.albumclassifier.data.db.entity.Category
import com.albumclassifier.data.db.entity.MediaItem
import com.albumclassifier.util.FileUtil
import kotlinx.coroutines.flow.Flow

class AlbumRepository(context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val categoryDao = db.categoryDao()
    private val albumDao = db.albumDao()
    private val mediaDao = db.mediaDao()
    private val appContext = context.applicationContext

    // ── Category ──

    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAll()

    suspend fun getCategoryById(id: Long): Category? = categoryDao.getById(id)

    suspend fun createCategory(name: String): Long {
        return categoryDao.insert(Category(name = name))
    }

    suspend fun updateCategory(category: Category) = categoryDao.update(category)

    suspend fun deleteCategory(category: Category) = categoryDao.delete(category)

    // ── Album ──

    fun getAlbumsByCategory(categoryId: Long): Flow<List<Album>> =
        albumDao.getByCategoryId(categoryId)

    suspend fun getAlbumById(id: Long): Album? = albumDao.getById(id)

    suspend fun createAlbum(categoryId: Long, name: String): Long {
        return albumDao.insert(Album(categoryId = categoryId, name = name))
    }

    suspend fun updateAlbum(album: Album) = albumDao.update(album)

    suspend fun deleteAlbum(album: Album) = albumDao.delete(album)

    // ── Media ──

    fun getMediaByAlbum(albumId: Long): Flow<List<MediaItem>> =
        mediaDao.getByAlbumId(albumId)

    suspend fun getMediaById(id: Long): MediaItem? = mediaDao.getById(id)

    /**
     * 从 URI 导入媒体文件到应用私有目录并记录到数据库
     */
    suspend fun importMedia(albumId: Long, sourceUris: List<Uri>): List<MediaItem> {
        val items = mutableListOf<MediaItem>()
        for (uri in sourceUris) {
            try {
                val copied = FileUtil.copyToPrivateDir(appContext, uri) ?: continue
                val mimeType = appContext.contentResolver.getType(uri) ?: "application/octet-stream"
                val fileName = FileUtil.getFileName(appContext, uri)

                val item = MediaItem(
                    albumId = albumId,
                    uri = copied.toString(),
                    mimeType = mimeType,
                    fileName = fileName,
                    fileSize = FileUtil.getFileSize(appContext, uri)
                )
                val id = mediaDao.insert(item)
                items.add(item.copy(id = id))

                // 自动设置相册封面
                val album = albumDao.getById(albumId)
                if (album?.coverUri == null) {
                    albumDao.updateCover(albumId, copied.toString())
                }
                // 自动设置分类封面
                if (album != null) {
                    val category = categoryDao.getById(album.categoryId)
                    if (category?.coverUri == null) {
                        categoryDao.updateCover(album.categoryId, copied.toString())
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return items
    }

    /**
     * 保存拍照结果到相册
     */
    suspend fun saveCapturedPhoto(albumId: Long, uri: Uri): MediaItem? {
        return try {
            val copied = FileUtil.copyToPrivateDir(appContext, uri) ?: return null
            val item = MediaItem(
                albumId = albumId,
                uri = copied.toString(),
                mimeType = "image/jpeg",
                fileName = "IMG_${System.currentTimeMillis()}.jpg"
            )
            val id = mediaDao.insert(item)

            val album = albumDao.getById(albumId)
            if (album?.coverUri == null) {
                albumDao.updateCover(albumId, copied.toString())
            }
            if (album != null) {
                val category = categoryDao.getById(album.categoryId)
                if (category?.coverUri == null) {
                    categoryDao.updateCover(album.categoryId, copied.toString())
                }
            }

            item.copy(id = id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteMedia(items: List<MediaItem>) {
        for (item in items) {
            FileUtil.deleteFile(appContext, Uri.parse(item.uri))
        }
        mediaDao.deleteByIds(items.map { it.id })
    }
}
