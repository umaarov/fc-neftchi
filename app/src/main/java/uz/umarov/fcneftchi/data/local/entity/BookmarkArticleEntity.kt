package uz.umarov.fcneftchi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmark_articles")
data class BookmarkArticleEntity(
    @PrimaryKey val url: String,
    val articleId: String,
    val title: String,
    val imageUrl: String,
    val date: String,
    val description: String,
    val category: String,
    val addedAt: Long,
)
