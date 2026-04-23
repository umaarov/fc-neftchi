package uz.umarov.fcneftchi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.local.entity.BookmarkArticleEntity

@Dao
interface BookmarkArticleDao {

    @Query("SELECT * FROM bookmark_articles ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<BookmarkArticleEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmark_articles WHERE url = :url)")
    fun observeIsBookmarked(url: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: BookmarkArticleEntity)

    @Query("DELETE FROM bookmark_articles WHERE url = :url")
    suspend fun remove(url: String)
}
