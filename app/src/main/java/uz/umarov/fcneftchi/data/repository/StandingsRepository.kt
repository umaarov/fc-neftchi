package uz.umarov.fcneftchi.data.repository

import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.model.LeagueStanding
import uz.umarov.fcneftchi.data.model.TopPlayer

interface StandingsRepository {
    fun getLeagueTable(seasonId: Int): Flow<List<LeagueStanding>>
    fun getTopPlayers(): Flow<List<TopPlayer>>
}
