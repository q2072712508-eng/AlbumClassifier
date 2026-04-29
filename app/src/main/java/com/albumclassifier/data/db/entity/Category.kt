package com.albumclassifier.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 一级分类（如：旅行、工作、生活）
 */
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val coverUri: String? = null,      // 封面图 URI
    val createdAt: Long = System.currentTimeMillis(),
    val sortOrder: Int = 0
)
