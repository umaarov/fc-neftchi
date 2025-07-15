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