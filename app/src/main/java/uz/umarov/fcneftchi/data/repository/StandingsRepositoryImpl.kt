package uz.umarov.fcneftchi.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import uz.umarov.fcneftchi.data.api.PflApiService
import uz.umarov.fcneftchi.data.local.dao.LeagueStandingDao
import uz.umarov.fcneftchi.data.mapper.toEntity
import uz.umarov.fcneftchi.data.mapper.toLeagueStanding
import uz.umarov.fcneftchi.data.model.LeagueStanding
import uz.umarov.fcneftchi.data.model.TopPlayer
import javax.inject.Inject

class StandingsRepositoryImpl @Inject constructor(
    private val apiService: PflApiService,
    private val dao: LeagueStandingDao
) : StandingsRepository {

    override fun getLeagueTable(seasonId: Int): Flow<List<LeagueStanding>> = flow {
        val cached = dao.observeForSeason(seasonId).first().map { it.toLeagueStanding() }
        if (cached.isNotEmpty()) emit(cached)
        try {
            val fresh = apiService.getLeagueTable(seasonId)
                .data.table.mapIndexed { index, item -> item.toLeagueStanding(position = index + 1) }
            val now = System.currentTimeMillis()
            dao.replaceSeason(seasonId, fresh.map { it.toEntity(seasonId, now) })
            emit(fresh)
        } catch (e: Exception) {
            Timber.tag("StandingsRepo").e(e, "League table API error")
            if (cached.isEmpty()) emit(emptyList())
        }
    }

    override fun getTopPlayers(): Flow<List<TopPlayer>> = flow {
        try {
            emit(apiService.getTopPlayers().data.scorers)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}
