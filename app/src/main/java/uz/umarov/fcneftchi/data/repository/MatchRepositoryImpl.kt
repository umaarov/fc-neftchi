package uz.umarov.fcneftchi.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import uz.umarov.fcneftchi.data.ClubConfig
import uz.umarov.fcneftchi.data.api.PflApiService
import uz.umarov.fcneftchi.data.local.dao.MatchDao
import uz.umarov.fcneftchi.data.mapper.toEntity
import uz.umarov.fcneftchi.data.mapper.toMatch
import uz.umarov.fcneftchi.data.model.GameCalendarMatch
import uz.umarov.fcneftchi.data.model.GameDetail
import uz.umarov.fcneftchi.data.model.Match
import uz.umarov.fcneftchi.util.DateUtils
import java.util.Date
import javax.inject.Inject

class MatchRepositoryImpl @Inject constructor(
    private val apiService: PflApiService,
    private val dao: MatchDao
) : MatchRepository {

    private suspend fun fetchAndCache(): List<Match> {
        val response = apiService.getGameCalendar(
            tournamentId = ClubConfig.TOURNAMENT_ID,
            seasonId = ClubConfig.CURRENT_SEASON_ID,
            clubId = ClubConfig.CLUB_ID
        )
        val games: List<GameCalendarMatch> = response.data.table.flatMap { it.matches }
        val matches = games.map { it.toMatch() }
        val now = System.currentTimeMillis()
        dao.replaceAll(matches.map { it.toEntity(now) })
        return matches
    }

    private suspend fun cachedMatches(): List<Match> =
        dao.observeAll().first().map { it.toMatch() }

    override fun getAllFixtures(): Flow<List<Match>> = flow {
        val now = Date()
        val cached = cachedMatches()
            .filter { DateUtils.parseDate(it.matchDate)?.after(now) != false }
            .sortedBy { it.matchDate }
        if (cached.isNotEmpty()) emit(cached)
        try {
            val fresh = fetchAndCache()
                .filter { DateUtils.parseDate(it.matchDate)?.after(now) != false }
                .sortedBy { it.matchDate }
            emit(fresh)
        } catch (e: Exception) {
            Timber.tag("MatchRepo").e(e, "Fixtures refresh failed")
            if (cached.isEmpty()) throw e
        }
    }

    override fun getAllResults(): Flow<List<Match>> = flow {
        val now = Date()
        val cached = cachedMatches()
            .filter { DateUtils.parseDate(it.matchDate)?.before(now) == true }
            .sortedByDescending { it.matchDate }
        if (cached.isNotEmpty()) emit(cached)
        try {
            val fresh = fetchAndCache()
                .filter { DateUtils.parseDate(it.matchDate)?.before(now) == true }
                .sortedByDescending { it.matchDate }
            emit(fresh)
        } catch (e: Exception) {
            Timber.tag("MatchRepo").e(e, "Results refresh failed")
            if (cached.isEmpty()) throw e
        }
    }

    override fun getNextMatch(): Flow<Match?> = flow {
        val now = Date()
        val cachedNext = cachedMatches()
            .filter { DateUtils.parseDate(it.matchDate)?.after(now) != false }
            .minByOrNull { it.matchDate }
        if (cachedNext != null) emit(cachedNext)
        try {
            val fresh = fetchAndCache()
                .filter { DateUtils.parseDate(it.matchDate)?.after(now) != false }
                .minByOrNull { it.matchDate }
            emit(fresh)
        } catch (e: Exception) {
            Timber.tag("MatchRepo").e(e, "Next match refresh failed")
            if (cachedNext == null) emit(null)
        }
    }

    override fun getLastMatch(): Flow<Match?> = flow {
        val now = Date()
        val cachedLast = cachedMatches()
            .filter { DateUtils.parseDate(it.matchDate)?.before(now) == true }
            .maxByOrNull { it.matchDate }
        if (cachedLast != null) emit(cachedLast)
        try {
            val fresh = fetchAndCache()
                .filter { DateUtils.parseDate(it.matchDate)?.before(now) == true }
                .maxByOrNull { it.matchDate }
            emit(fresh)
        } catch (e: Exception) {
            Timber.tag("MatchRepo").e(e, "Last match refresh failed")
            if (cachedLast == null) emit(null)
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
