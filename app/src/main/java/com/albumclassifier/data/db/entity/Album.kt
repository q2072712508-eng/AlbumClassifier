package com.albumclassifier.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 二级相册（如：日本2024、云南2025）
 */
@Entity(
    tableName = "albums",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId")]
)
data class Album(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoryId: Long,
    val name: String,
    val coverUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val sortOrder: Int = 0
)
