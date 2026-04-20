package uz.umarov.fcneftchi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_articles")
data class NewsArticleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val imageUrl: String,
    val date: String,
    val content: String,
    val description: String,
    val category: String,
    val url: String,
    val sortOrder: Int,
    val cachedAt: Long
)
