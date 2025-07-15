package uz.umarov.fcneftchi.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.umarov.fcneftchi.data.api.PflApiService
import uz.umarov.fcneftchi.data.model.ApiPlayer
import uz.umarov.fcneftchi.data.model.LeagueStanding
import uz.umarov.fcneftchi.data.model.Match
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.data.model.Player
import uz.umarov.fcneftchi.data.model.Squad
import uz.umarov.fcneftchi.data.model.StatisticsData
import uz.umarov.fcneftchi.data.model.Video
import javax.inject.Inject

class RealNeftchiRepository @Inject constructor(
    private val apiService: PflApiService
) : NeftchiRepository {

    private val neftchiClubId = 7

    override fun getClubStatistics(): Flow<StatisticsData?> = flow {
        try {
            val response = apiService.getClubStatistics(neftchiClubId)
            emit(response.data)
        } catch (e: Exception) {
            emit(null)
        }
    }

    override fun getSquads(): Flow<List<Squad>> = flow {
        coroutineScope {
            val clubDetails = apiService.getClubDetails(neftchiClubId)

            val squadData = clubDetails.data.clubTeams.map { team ->
                async {
                    val playersResponse = apiService.getClubPlayers(neftchiClubId, team.id)
                    val uiPlayers = playersResponse.data.players.map { apiPlayer ->
                        mapApiPlayerToUiPlayer(apiPlayer)
                    }
                    Squad(teamName = team.title, players = uiPlayers)
                }
            }.map { it.await() }

            emit(squadData)
        }
    }

    override fun getTeam(): Flow<List<Player>> = flow {
        val mainTeamId = 33
        val apiPlayers = apiService.getClubPlayers(neftchiClubId, mainTeamId).data.players
        val uiPlayers = apiPlayers.map { mapApiPlayerToUiPlayer(it) }
        emit(uiPlayers)
    }

    private fun mapApiPlayerToUiPlayer(apiPlayer: ApiPlayer): Player {
        return Player(
            id = apiPlayer.id,
            name = "${apiPlayer.firstName ?: ""} ${apiPlayer.lastName}".trim(),
            number = apiPlayer.number ?: 0,
            position = mapPosition(apiPlayer.position),
            imageUrl = apiPlayer.photo
                ?: "https://placehold.co/400x400/333333/FFFFFF?text=No+Image",
            nationality = apiPlayer.countryTitle
        )
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