package uz.umarov.fcneftchi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.local.entity.BookmarkVideoEntity

@Dao
interface BookmarkVideoDao {

    @Query("SELECT * FROM bookmark_videos ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<BookmarkVideoEntity>>

    @Query("SELECT id FROM bookmark_videos")
    fun observeBookmarkedIds(): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmark_videos WHERE id = :id)")
    fun observeIsBookmarked(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: BookmarkVideoEntity)

    @Query("DELETE FROM bookmark_videos WHERE id = :id")
    suspend fun remove(id: String)
}
