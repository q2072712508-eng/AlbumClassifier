package com.albumclassifier.data.db.dao

import androidx.room.*
import com.albumclassifier.data.db.entity.MediaItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {

    @Query("SELECT * FROM media_items WHERE albumId = :albumId ORDER BY addedAt DESC")
    fun getByAlbumId(albumId: Long): Flow<List<MediaItem>>

    @Query("SELECT * FROM media_items WHERE albumId = :albumId ORDER BY addedAt DESC LIMIT :limit")
    suspend fun getByAlbumIdLimit(albumId: Long, limit: Int): List<MediaItem>

    @Query("SELECT * FROM media_items WHERE id = :id")
    suspend fun getById(id: Long): MediaItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mediaItem: MediaItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MediaItem>)

    @Delete
    suspend fun delete(mediaItem: MediaItem)

    @Query("DELETE FROM media_items WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)

    @Query("SELECT COUNT(*) FROM media_items WHERE albumId = :albumId")
    suspend fun countByAlbum(albumId: Long): Int
}
