package uz.umarov.fcneftchi.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.umarov.fcneftchi.data.ClubConfig
import uz.umarov.fcneftchi.data.api.PflApiService
import uz.umarov.fcneftchi.data.mapper.toMatch
import uz.umarov.fcneftchi.data.model.GameCalendarMatch
import uz.umarov.fcneftchi.data.model.GameDetail
import uz.umarov.fcneftchi.data.model.Match
import uz.umarov.fcneftchi.util.DateUtils
import java.util.Date
import javax.inject.Inject

class MatchRepositoryImpl @Inject constructor(
    private val apiService: PflApiService
) : MatchRepository {

    private suspend fun loadCalendar(): List<GameCalendarMatch> {
        val response = apiService.getGameCalendar(
            tournamentId = ClubConfig.TOURNAMENT_ID,
            seasonId = ClubConfig.CURRENT_SEASON_ID,
            clubId = ClubConfig.CLUB_ID
        )
        return response.data.table.flatMap { it.matches }
    }

    override fun getAllFixtures(): Flow<List<Match>> = flow {
        val games = loadCalendar()
        val now = Date()
        emit(
            games
                .filter { DateUtils.parseDate(it.startDate)?.after(now) != false }
                .sortedBy { it.startDate }
                .map { it.toMatch() }
        )
    }

    override fun getAllResults(): Flow<List<Match>> = flow {
        val games = loadCalendar()
        val now = Date()
        emit(
            games
                .filter { DateUtils.parseDate(it.startDate)?.before(now) == true }
                .sortedByDescending { it.startDate }
                .map { it.toMatch() }
        )
    }

    override fun getNextMatch(): Flow<Match?> = flow {
        try {
            val games = loadCalendar()
            val now = Date()
            val next = games
                .filter { DateUtils.parseDate(it.startDate)?.after(now) != false }
                .minByOrNull { it.startDate }
            emit(next?.toMatch())
        } catch (e: Exception) {
            emit(null)
        }
    }

    override fun getLastMatch(): Flow<Match?> = flow {
        try {
            val games = loadCalendar()
            val now = Date()
            val last = games
                .filter { DateUtils.parseDate(it.startDate)?.before(now) == true }
                .maxByOrNull { it.startDate }
            emit(last?.toMatch())
        } catch (e: Exception) {
            emit(null)
        }
    }

    override fun getGameDetails(gameId: Int): Flow<GameDetail?> = flow {
        try {
            emit(apiService.getGameDetails(gameId).data)
        } catch (e: Exception) {
            emit(null)
        }
    }
}
