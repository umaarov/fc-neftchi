package uz.umarov.fcneftchi.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import uz.umarov.fcneftchi.data.api.PflApiService
import uz.umarov.fcneftchi.data.mapper.toLeagueStanding
import uz.umarov.fcneftchi.data.model.LeagueStanding
import uz.umarov.fcneftchi.data.model.TopPlayer
import javax.inject.Inject

class StandingsRepositoryImpl @Inject constructor(
    private val apiService: PflApiService
) : StandingsRepository {

    override fun getLeagueTable(seasonId: Int): Flow<List<LeagueStanding>> = flow {
        val response = apiService.getLeagueTable(seasonId)
        emit(
            response.data.table.mapIndexed { index, item ->
                item.toLeagueStanding(position = index + 1)
            }
        )
    }.catch { e ->
        Timber.tag("StandingsRepo").e(e, "League table API error")
        emit(emptyList())
    }

    override fun getTopPlayers(): Flow<List<TopPlayer>> = flow {
        try {
            emit(apiService.getTopPlayers().data.scorers)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}
