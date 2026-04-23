package uz.umarov.fcneftchi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmark_videos")
data class BookmarkVideoEntity(
    @PrimaryKey val id: String,
    val title: String,
    val thumbnailUrl: String,
    val videoUrl: String,
    val category: String,
    val date: String,
    val duration: String,
    val addedAt: Long,
)
