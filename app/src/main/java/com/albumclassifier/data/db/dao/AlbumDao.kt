package com.albumclassifier.data.db.dao

import androidx.room.*
import com.albumclassifier.data.db.entity.Album
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {

    @Query("SELECT * FROM albums WHERE categoryId = :categoryId ORDER BY sortOrder ASC, createdAt DESC")
    fun getByCategoryId(categoryId: Long): Flow<List<Album>>

    @Query("SELECT * FROM albums WHERE id = :id")
    suspend fun getById(id: Long): Album?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(album: Album): Long

    @Update
    suspend fun update(album: Album)

    @Delete
    suspend fun delete(album: Album)

    @Query("SELECT COUNT(*) FROM albums WHERE categoryId = :categoryId")
    suspend fun countByCategory(categoryId: Long): Int

    @Query("UPDATE albums SET coverUri = :uri WHERE id = :id")
    suspend fun updateCover(id: Long, uri: String?)
}
