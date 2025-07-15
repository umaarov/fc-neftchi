package uz.umarov.fcneftchi.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.umarov.fcneftchi.data.api.PflApiService
import uz.umarov.fcneftchi.data.model.ApiGame
import uz.umarov.fcneftchi.data.model.ApiPlayer
import uz.umarov.fcneftchi.data.model.GameCalendarMatch
import uz.umarov.fcneftchi.data.model.GameDetail
import uz.umarov.fcneftchi.data.model.LeagueStanding
import uz.umarov.fcneftchi.data.model.Match
import uz.umarov.fcneftchi.data.model.MatchStatus
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.data.model.Player
import uz.umarov.fcneftchi.data.model.PlayerProfile
import uz.umarov.fcneftchi.data.model.Squad
import uz.umarov.fcneftchi.data.model.StatisticsData
import uz.umarov.fcneftchi.data.model.Team
import uz.umarov.fcneftchi.data.model.TopPlayer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class RealNeftchiRepository @Inject constructor(
    private val apiService: PflApiService
) : NeftchiRepository {

    private val neftchiClubId = 7
    private val superligaTournamentId = 1
    private val currentSeasonId = 10

    private suspend fun getAllGamesFromCalendar(): List<GameCalendarMatch> {
        val response = apiService.getGameCalendar(superligaTournamentId, currentSeasonId, neftchiClubId)
        return response.data.table.flatMap { it.matches }
    }

    override fun getNews(): Flow<List<NewsArticle>> = flow {
        val response = apiService.getNews(neftchiClubId)
        val articles = response.data.list.map { newsItem ->
            NewsArticle(
                id = newsItem.id.toString(),
                title = newsItem.contents.title,
                imageUrl = newsItem.image
                    ?: "https://placehold.co/600x400/CCCCCC/FFFFFF?text=No+Image",
                date = formatApiDate(newsItem.publicDate),
                content = newsItem.contents.description ?: "",
                url = newsItem.contents.url
            )
        }
        emit(articles)
    }

    override fun getNewsArticleByUrl(url: String): Flow<NewsArticle?> = flow {
        try {
            val response = apiService.getNewsDetail(url)
            val detailData = response.data
            val fullContentHtml = detailData.text.joinToString(separator = "") { textItem ->
                textItem.value ?: ""
            }
            val article = NewsArticle(
                id = detailData.id.toString(),
                title = detailData.title,
                imageUrl = detailData.image
                    ?: "https://placehold.co/600x400/CCCCCC/FFFFFF?text=No+Image",
                date = formatApiDate(detailData.publicDate),
                content = fullContentHtml,
                url = url
            )
            emit(article)
        } catch (e: Exception) {
            emit(null)
        }
    }

    private fun formatApiDate(dateString: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val date = parser.parse(dateString)
            val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            date?.let { formatter.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

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
        coroutineScope {
            val clubDetails = apiService.getClubDetails(neftchiClubId)
            val playersResponse = apiService.getClubPlayers(neftchiClubId, clubDetails.data.clubTeams.first().id)
            val uiPlayers = playersResponse.data.players.map { apiPlayer ->
                mapApiPlayerToUiPlayer(apiPlayer)
            }
            emit(uiPlayers)
        }
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
            1 -> "Darvozabon"
            2 -> "Himoyachi"
            3 -> "Yarim himoyachi"
            4 -> "Hujumchi"
            else -> "Noma'lum"
        }
    }

    override fun getPlayerProfile(playerId: Int): Flow<PlayerProfile?> = flow {
        coroutineScope {
            try {
                val detailsDeferred = async { apiService.getPlayerDetails(playerId) }
                val statsDeferred = async { apiService.getPlayerStatistics(playerId) }

                val detailsResponse = detailsDeferred.await()
                val statsResponse = statsDeferred.await()

                val playerProfile = PlayerProfile(
                    details = detailsResponse.data,
                    stats = statsResponse.data.statistic,
                    career = statsResponse.data.carrier
                )
                emit(playerProfile)
            } catch (e: Exception) {
                emit(null)
            }
        }
    }

    override fun getAllFixtures(): Flow<List<Match>> = flow {
        val allGames = getAllGamesFromCalendar()
        val now = Date()
        val fixtures = allGames
            .filter { parseDate(it.startDate)?.after(now) ?: true }
            .sortedBy { it.startDate }
            .map { mapApiGameToMatch(it) }
        emit(fixtures)
    }

    override fun getAllResults(): Flow<List<Match>> = flow {
        val allGames = getAllGamesFromCalendar()
        val now = Date()
        val results = allGames
            .filter { parseDate(it.startDate)?.before(now) ?: false }
            .sortedByDescending { it.startDate }
            .map { mapApiGameToMatch(it) }
        emit(results)
    }


    private fun mapApiGameToMatch(apiGame: GameCalendarMatch): Match {
        val homeTeam = Team(
            id = apiGame.homeTeam.club.id,
            name = apiGame.homeTeam.club.title,
            logoUrl = apiGame.homeTeam.club.logo
        )
        val awayTeam = Team(
            id = apiGame.awayTeam.club.id,
            name = apiGame.awayTeam.club.title,
            logoUrl = apiGame.awayTeam.club.logo
        )
        val status = if (parseDate(apiGame.startDate)?.after(Date()) == true) {
            MatchStatus.SCHEDULED
        } else {
            MatchStatus.FINISHED
        }

        return Match(
            id = apiGame.id.toString(),
            homeTeam = homeTeam,
            awayTeam = awayTeam,
            homeScore = apiGame.homeGoal,
            awayScore = apiGame.awayGoal,
            matchDate = apiGame.startDate,
            status = status,
            competition = "Superliga"
        )
    }

    override fun getNextMatch(): Flow<Match> = flow {
        val allGames = getAllGamesFromCalendar()
        val now = Date()
        val nextFixture = allGames
            .filter { parseDate(it.startDate)?.after(now) ?: true }
            .minByOrNull { it.startDate }

        if (nextFixture != null) {
            emit(mapApiGameToMatch(nextFixture))
        }
    }

    override fun getLastMatch(): Flow<Match> = flow {
        val allGames = getAllGamesFromCalendar()
        val now = Date()
        val lastResult = allGames
            .filter { parseDate(it.startDate)?.before(now) ?: false }
            .maxByOrNull { it.startDate }

        if (lastResult != null) {
            emit(mapApiGameToMatch(lastResult))
        }
    }

    private fun parseDate(dateString: String): Date? {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            parser.timeZone = TimeZone.getTimeZone("UTC")
            parser.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    override fun getLeagueTable(): Flow<List<LeagueStanding>> = flow {
        val response = apiService.getLeagueTable()
        val standings = response.data.table.mapIndexed { index, tableItem ->
            LeagueStanding(
                position = index + 1,
                team = Team(
                    id = tableItem.id,
                    name = tableItem.title,
                    logoUrl = tableItem.logo
                ),
                played = tableItem.games,
                wins = tableItem.wins,
                draws = tableItem.draws,
                losses = tableItem.losses,
                points = tableItem.points
            )
        }
        emit(standings)
    }

    override fun getTopPlayers(): Flow<List<TopPlayer>> = flow {
        try {
            val response = apiService.getTopPlayers()
            emit(response.data.scorers)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override fun getGameDetails(gameId: Int): Flow<GameDetail?> = flow {
        try {
            val response = apiService.getGameDetails(gameId)
            emit(response.data)
        } catch (e: Exception) {
            emit(null)
        }
    }
}
