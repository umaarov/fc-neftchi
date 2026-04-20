package uz.umarov.fcneftchi.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import uz.umarov.fcneftchi.data.ClubConfig
import uz.umarov.fcneftchi.data.api.PflApiService
import uz.umarov.fcneftchi.data.local.dao.PlayerDao
import uz.umarov.fcneftchi.data.mapper.toEntity
import uz.umarov.fcneftchi.data.mapper.toPlayer
import uz.umarov.fcneftchi.data.model.Player
import uz.umarov.fcneftchi.data.model.PlayerProfile
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(
    private val apiService: PflApiService,
    private val dao: PlayerDao
) : PlayerRepository {

    override fun getSquad(): Flow<List<Player>> = flow {
        val cached = dao.observeAll().first().map { it.toPlayer() }
        if (cached.isNotEmpty()) emit(cached)
        try {
            val fresh = apiService.getClubPlayers(ClubConfig.CLUB_ID)
                .data.players.distinctBy { it.id }.map { it.toPlayer() }
            val now = System.currentTimeMillis()
            dao.replaceAll(fresh.map { it.toEntity(now) })
            emit(fresh)
        } catch (e: Exception) {
            Timber.tag("PlayerRepo").e(e, "Squad refresh failed")
            if (cached.isEmpty()) emit(emptyList())
        }
    }

    override fun getPlayerProfile(playerId: Int): Flow<PlayerProfile?> = flow {
        coroutineScope {
            try {
                val detailsDeferred = async { apiService.getPlayerDetails(playerId) }
                val statsDeferred = async { apiService.getPlayerStatistics(playerId) }

                val detailsResponse = detailsDeferred.await()
                val statsResponse = statsDeferred.await()

                emit(
                    PlayerProfile(
                        details = detailsResponse.data,
                        stats = statsResponse.data.statistic,
                        career = statsResponse.data.carrier
                    )
                )
            } catch (e: Exception) {
                emit(null)
            }
        }
    }
}
