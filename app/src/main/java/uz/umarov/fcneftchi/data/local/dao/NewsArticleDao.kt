package uz.umarov.fcneftchi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.local.entity.NewsArticleEntity

@Dao
interface NewsArticleDao {

    @Query("SELECT * FROM news_articles ORDER BY sortOrder ASC")
    fun observeAll(): Flow<List<NewsArticleEntity>>

    @Query("SELECT * FROM news_articles WHERE url = :url LIMIT 1")
    fun observeByUrl(url: String): Flow<NewsArticleEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(articles: List<NewsArticleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: NewsArticleEntity)

    @Query("DELETE FROM news_articles")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(articles: List<NewsArticleEntity>) {
        clear()
        upsertAll(articles)
    }
}
