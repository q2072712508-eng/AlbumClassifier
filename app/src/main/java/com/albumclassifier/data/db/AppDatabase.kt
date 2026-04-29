package com.albumclassifier.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.albumclassifier.data.db.dao.AlbumDao
import com.albumclassifier.data.db.dao.CategoryDao
import com.albumclassifier.data.db.dao.MediaDao
import com.albumclassifier.data.db.entity.Album
import com.albumclassifier.data.db.entity.Category
import com.albumclassifier.data.db.entity.MediaItem

@Database(
    entities = [Category::class, Album::class, MediaItem::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun albumDao(): AlbumDao
    abstract fun mediaDao(): MediaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "album_classifier.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
