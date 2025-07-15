package uz.umarov.fcneftchi.data.model

data class Team(
    val id: Int,
    val name: String,
    val logoUrl: String
)

data class Match(
    val id: String,
    val homeTeam: Team,
    val awayTeam: Team,
    val homeScore: Int?,
    val awayScore: Int?,
    val matchDate: String,
    val status: MatchStatus,
    val competition: String
)

enum class MatchStatus {
    SCHEDULED, LIVE, FINISHED
}

data class Player(
    val id: Int,
    val name: String,
    val number: Int,
    val position: String,
    val imageUrl: String,
    val nationality: String
)

data class NewsArticle(
    val id: String,
    val title: String,
    val imageUrl: String,
    val date: String,
    val content: String
)

data class LeagueStanding(
    val position: Int,
    val team: Team,
    val played: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val points: Int
)

data class Video(
    val id: String,
    val title: String,
    val thumbnailUrl: String,
    val videoUrl: String,
    val duration: String
)
