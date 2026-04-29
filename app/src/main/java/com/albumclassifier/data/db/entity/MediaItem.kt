package com.albumclassifier.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 媒体项（图片/视频）
 */
@Entity(
    tableName = "media_items",
    foreignKeys = [
        ForeignKey(
            entity = Album::class,
            parentColumns = ["id"],
            childColumns = ["albumId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("albumId")]
)
data class MediaItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val albumId: Long,
    val uri: String,               // 文件 URI
    val mimeType: String,          // image/jpeg, video/mp4 等
    val fileName: String,
    val fileSize: Long = 0,
    val width: Int = 0,
    val height: Int = 0,
    val duration: Long = 0,        // 视频时长(ms)，图片为0
    val addedAt: Long = System.currentTimeMillis()
) {
    val isVideo: Boolean get() = mimeType.startsWith("video/")
    val isImage: Boolean get() = mimeType.startsWith("image/")
}
