package uz.umarov.fcneftchi.data.model

import com.squareup.moshi.Json

data class PlayerApiResponse(
    @Json(name = "data") val data: PlayerData
)

data class PlayerData(
    @Json(name = "players") val players: List<ApiPlayer>
)

data class ApiPlayer(
    @Json(name = "id") val id: Int,
    @Json(name = "position") val position: Int,
    @Json(name = "first_name") val firstName: String?,
    @Json(name = "last_name") val lastName: String,
    @Json(name = "photo") val photo: String?,
    @Json(name = "number") val number: Int?,
    @Json(name = "country_title") val countryTitle: String
)

data class ClubDetailResponse(
    @Json(name = "data") val data: ClubDetails
)

data class ClubDetails(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "clubTeams") val clubTeams: List<ClubTeam>
)

data class ClubTeam(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String
)

data class StatisticsResponse(
    @Json(name = "data") val data: StatisticsData
)

data class StatisticsData(
    @Json(name = "statistics") val clubStats: ClubStatistics,
    @Json(name = "players") val playerStats: List<PlayerStatistics>
)

data class ClubStatistics(
    @Json(name = "total_matches") val totalMatches: Int,
    @Json(name = "total_wins") val totalWins: Int,
    @Json(name = "total_draws") val totalDraws: Int,
    @Json(name = "total_losses") val totalLosses: Int,
    @Json(name = "total_goals_scored") val goalsScored: Int,
    @Json(name = "total_goals_conceded") val goalsConceded: Int
)

data class PlayerStatistics(
    @Json(name = "id") val id: Int,
    @Json(name = "first_name") val firstName: String?,
    @Json(name = "last_name") val lastName: String,
    @Json(name = "photo") val photo: String?,
    @Json(name = "number") val number: Int?,
    @Json(name = "games") val games: Int,
    @Json(name = "minute") val minutes: Int,
    @Json(name = "goals") val goals: Int,
    @Json(name = "assists") val assists: Int,
    @Json(name = "card_y") val yellowCards: Int,
    @Json(name = "card_r") val redCards: Int
)

data class Squad(
    val teamName: String,
    val players: List<Player>
)

data class NewsApiResponse(
    @Json(name = "data") val data: NewsData
)

data class NewsData(
    @Json(name = "list") val list: List<NewsListItem>
)


data class NewsListItem(
    @Json(name = "id") val id: Int,
    @Json(name = "image") val image: String?,
    @Json(name = "publicDate") val publicDate: String,
    @Json(name = "contents") val contents: NewsContents,
    @Json(name = "category") val category: NewsCategory
)

data class NewsCategory(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String
)

data class NewsContents(
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String?,
    @Json(name = "url") val url: String
)

data class NewsDetailResponse(
    @Json(name = "data") val data: NewsDetailData
)

data class NewsDetailData(
    @Json(name = "id") val id: Int,
    @Json(name = "image") val image: String?,
    @Json(name = "publicDate") val publicDate: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String?,
    @Json(name = "category") val category: NewsCategory?,
    @Json(name = "text") val text: List<NewsTextItem>
)

data class NewsTextItem(
    @Json(name = "type") val type: String,
    @Json(name = "value") val value: String?
)

data class NewsArticle(
    val id: String,
    val title: String,
    val imageUrl: String,
    val date: String,
    val content: String,
    val description: String,
    val category: String,
    val url: String
)

data class PlayerDetailResponse(
    @Json(name = "data") val data: PlayerDetail
)

data class PlayerDetail(
    @Json(name = "id") val id: Int,
    @Json(name = "firstName") val firstName: String?,
    @Json(name = "lastName") val lastName: String,
    @Json(name = "birthday") val birthday: String?,
    @Json(name = "photo") val photo: String?,
    @Json(name = "position") val position: Int,
    @Json(name = "number") val number: Int?,
    @Json(name = "country") val country: PlayerCountry
)

data class PlayerCountry(
    @Json(name = "title") val title: String
)

data class PlayerStatisticResponse(
    @Json(name = "data") val data: PlayerStatisticData
)

data class PlayerStatisticData(
    @Json(name = "statistic") val statistic: PlayerSeasonStats,
    @Json(name = "carrier") val carrier: List<PlayerCareerItem>
)

data class PlayerSeasonStats(
    @Json(name = "goal") val goals: Int,
    @Json(name = "assists") val assists: Int,
    @Json(name = "yellow_card") val yellowCards: Int,
    @Json(name = "red_card") val redCards: Int,
    @Json(name = "games_in") val games: Int,
    @Json(name = "minute_in_game") val minutes: Int
)

data class PlayerCareerItem(
    @Json(name = "title") val clubName: String,
    @Json(name = "logo") val clubLogo: String?,
    @Json(name = "year") val year: Int,
    @Json(name = "games") val games: Int,
    @Json(name = "goals") val goals: Int
)

data class PlayerProfile(
    val details: PlayerDetail,
    val stats: PlayerSeasonStats,
    val career: List<PlayerCareerItem>
)

data class GameApiResponse(
    @Json(name = "data") val data: GameData
)

data class GameData(
    @Json(name = "list") val list: List<ApiGame>
)

data class ApiGame(
    @Json(name = "id") val id: Int,
    @Json(name = "startDate") val startDate: String,
    @Json(name = "endDate") val endDate: String?,
    @Json(name = "homeTeam") val homeTeam: ApiGameTeam,
    @Json(name = "awayTeam") val awayTeam: ApiGameTeam,
    @Json(name = "homeGoal") val homeGoal: Int,
    @Json(name = "awayGoal") val awayGoal: Int
)

data class ApiGameTeam(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "club") val club: ApiGameClub
)

data class ApiGameClub(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "logo") val logo: String
)

data class LeagueTableResponse(
    @Json(name = "data") val data: LeagueTableData
)

data class LeagueTableData(
    @Json(name = "table") val table: List<LeagueTableItem>
)

data class LeagueTableItem(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "logo") val logo: String,
    @Json(name = "c_games") val games: Int,
    @Json(name = "c_games_vic") val wins: Int,
    @Json(name = "c_games_drw") val draws: Int,
    @Json(name = "c_games_def") val losses: Int,
    @Json(name = "c_goal_tf") val goalDifference: Int,
    @Json(name = "c_point") val points: Int
)

data class TopPlayersResponse(
    @Json(name = "data") val data: TopPlayersData
)

data class TopPlayersData(
    @Json(name = "scorers") val scorers: List<TopPlayer>
)

data class TopPlayer(
    @Json(name = "player_id") val playerId: Int,
    @Json(name = "player_first_name") val firstName: String?,
    @Json(name = "player_last_name") val lastName: String,
    @Json(name = "player_photo") val photo: String?,
    @Json(name = "club_title") val clubTitle: String,
    @Json(name = "club_logo") val clubLogo: String?,
    @Json(name = "goals") val goals: Int,
    @Json(name = "assists") val assists: Int
)

data class GameDetailResponse(
    @Json(name = "data") val data: GameDetail
)

data class GameDetail(
    @Json(name = "id") val id: Int,
    @Json(name = "startDate") val startDate: String,
    @Json(name = "homeTeam") val homeTeam: ApiGameTeam,
    @Json(name = "awayTeam") val awayTeam: ApiGameTeam,
    @Json(name = "homeGoal") val homeGoal: Int,
    @Json(name = "awayGoal") val awayGoal: Int,
    @Json(name = "events") val events: List<GameEvent>,
    @Json(name = "players") val players: List<GameLineupPlayer>,
    @Json(name = "statistics") val statistics: GameStatistics?
)

data class GameEvent(
    @Json(name = "id") val id: Int,
    @Json(name = "clubId") val clubId: Int,
    @Json(name = "primaryPlayerId") val primaryPlayerId: Int?,
    @Json(name = "secondaryPlayerId") val secondaryPlayerId: Int?,
    @Json(name = "time") val time: Int,
    @Json(name = "extraTime") val extraTime: Int?,
    @Json(name = "type") val type: Int
)

data class GameLineupPlayer(
    @Json(name = "status") val status: Int,
    @Json(name = "clubId") val clubId: Int,
    @Json(name = "player") val player: LineupPlayerDetail
)

data class LineupPlayerDetail(
    @Json(name = "id") val id: Int,
    @Json(name = "firstName") val firstName: String?,
    @Json(name = "lastName") val lastName: String,
    @Json(name = "clubPlayers") val clubPlayers: List<ClubPlayerNumber>
)

data class ClubPlayerNumber(
    @Json(name = "number") val number: Int
)

data class GameStatistics(
    @Json(name = "fouls_home") val foulsHome: Int,
    @Json(name = "fouls_away") val foulsAway: Int,
    @Json(name = "shots_home") val shotsHome: Int,
    @Json(name = "shots_away") val shotsAway: Int,
    @Json(name = "accurate_shots_home") val shotsOnTargetHome: Int,
    @Json(name = "accurate_shots_away") val shotsOnTargetAway: Int,
    @Json(name = "offside_home") val offsideHome: Int,
    @Json(name = "offside_away") val offsideAway: Int,
    @Json(name = "warning_home") val yellowCardsHome: Int,
    @Json(name = "warning_away") val yellowCardsAway: Int,
    @Json(name = "corner_kicks_home") val cornersHome: Int,
    @Json(name = "corner_kicks_away") val cornersAway: Int
)

data class GameCalendarResponse(
    @Json(name = "data") val data: GameCalendarData
)

data class GameCalendarData(
    @Json(name = "table") val table: List<GameCalendarTour>
)

data class GameCalendarTour(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "matches") val matches: List<GameCalendarMatch>
)

data class GameCalendarMatch(
    @Json(name = "id") val id: Int,
    @Json(name = "startDate") val startDate: String,
    @Json(name = "endDate") val endDate: String?,
    @Json(name = "homeTeam") val homeTeam: ApiGameTeam,
    @Json(name = "awayTeam") val awayTeam: ApiGameTeam,
    @Json(name = "homeGoal") val homeGoal: Int,
    @Json(name = "awayGoal") val awayGoal: Int
)
