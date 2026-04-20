package uz.umarov.fcneftchi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.local.entity.MatchEntity

@Dao
interface MatchDao {

    @Query("SELECT * FROM matches ORDER BY matchDate ASC")
    fun observeAll(): Flow<List<MatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(matches: List<MatchEntity>)

    @Query("DELETE FROM matches")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(matches: List<MatchEntity>) {
        clear()
        upsertAll(matches)
    }
}
