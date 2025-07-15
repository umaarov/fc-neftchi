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

data class Squad(
    val teamName: String,
    val players: List<Player>
)