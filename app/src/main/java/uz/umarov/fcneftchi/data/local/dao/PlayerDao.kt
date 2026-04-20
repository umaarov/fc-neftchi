package uz.umarov.fcneftchi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.local.entity.PlayerEntity

@Dao
interface PlayerDao {

    @Query("SELECT * FROM players ORDER BY number ASC")
    fun observeAll(): Flow<List<PlayerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(players: List<PlayerEntity>)

    @Query("DELETE FROM players")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(players: List<PlayerEntity>) {
        clear()
        upsertAll(players)
    }
}
