package uz.umarov.fcneftchi.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.umarov.fcneftchi.data.ClubConfig
import uz.umarov.fcneftchi.data.api.PflApiService
import uz.umarov.fcneftchi.data.mapper.toPlayer
import uz.umarov.fcneftchi.data.model.Player
import uz.umarov.fcneftchi.data.model.PlayerProfile
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(
    private val apiService: PflApiService
) : PlayerRepository {

    override fun getSquad(): Flow<List<Player>> = flow {
        try {
            val apiPlayers = apiService.getClubPlayers(ClubConfig.CLUB_ID).data.players
            emit(apiPlayers.distinctBy { it.id }.map { it.toPlayer() })
        } catch (e: Exception) {
            emit(emptyList())
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
