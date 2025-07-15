package uz.umarov.fcneftchi.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.umarov.fcneftchi.data.api.PflApiService
import uz.umarov.fcneftchi.data.model.*
import javax.inject.Inject

class RealNeftchiRepository @Inject constructor(
    private val apiService: PflApiService
) : NeftchiRepository {

    override fun getTeam(): Flow<List<Player>> = flow {
        val neftchiClubId = 7
        val apiPlayers = apiService.getClubPlayers(neftchiClubId).data.players

        val uiPlayers = apiPlayers.map { apiPlayer ->
            Player(
                id = apiPlayer.id,
                name = "${apiPlayer.firstName ?: ""} ${apiPlayer.lastName}".trim(),
                number = apiPlayer.number ?: 0,
                position = mapPosition(apiPlayer.position),
                imageUrl = apiPlayer.photo ?: "https://placehold.co/400x400/333333/FFFFFF?text=No+Image",
                nationality = apiPlayer.countryTitle
            )
        }
        emit(uiPlayers)
    }

    private fun mapPosition(positionId: Int): String {
        return when (positionId) {
            1 -> "Goalkeeper"
            2 -> "Defender"
            3 -> "Midfielder"
            4 -> "Forward"
            else -> "Unknown"
        }
    }

    override fun getNextMatch(): Flow<Match> = flow { }
    override fun getLastMatch(): Flow<Match> = flow { }
    override fun getAllFixtures(): Flow<List<Match>> = flow { emit(emptyList()) }
    override fun getAllResults(): Flow<List<Match>> = flow { emit(emptyList()) }
    override fun getNews(): Flow<List<NewsArticle>> = flow { emit(emptyList()) }
    override fun getLeagueTable(): Flow<List<LeagueStanding>> = flow { emit(emptyList()) }
    override fun getNewsArticleById(id: String): Flow<NewsArticle?> = flow { emit(null) }
    override fun getVideos(): Flow<List<Video>> = flow { emit(emptyList()) }
}