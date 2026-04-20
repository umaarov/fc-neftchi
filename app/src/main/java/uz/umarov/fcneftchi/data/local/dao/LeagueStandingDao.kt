package uz.umarov.fcneftchi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.local.entity.LeagueStandingEntity

@Dao
interface LeagueStandingDao {

    @Query("SELECT * FROM league_standings WHERE seasonId = :seasonId ORDER BY position ASC")
    fun observeForSeason(seasonId: Int): Flow<List<LeagueStandingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(rows: List<LeagueStandingEntity>)

    @Query("DELETE FROM league_standings WHERE seasonId = :seasonId")
    suspend fun clearSeason(seasonId: Int)

    @Transaction
    suspend fun replaceSeason(seasonId: Int, rows: List<LeagueStandingEntity>) {
        clearSeason(seasonId)
        upsertAll(rows)
    }
}
