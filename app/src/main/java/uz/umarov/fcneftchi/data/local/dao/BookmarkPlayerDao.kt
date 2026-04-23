package uz.umarov.fcneftchi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.local.entity.BookmarkPlayerEntity

@Dao
interface BookmarkPlayerDao {

    @Query("SELECT * FROM bookmark_players ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<BookmarkPlayerEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmark_players WHERE id = :id)")
    fun observeIsBookmarked(id: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: BookmarkPlayerEntity)

    @Query("DELETE FROM bookmark_players WHERE id = :id")
    suspend fun remove(id: Int)
}
