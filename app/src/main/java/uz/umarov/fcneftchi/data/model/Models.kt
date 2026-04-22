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
    val position: PlayerPosition,
    val imageUrl: Any,
    val nationality: String
)

enum class PlayerPosition(val positionId: Int) {
    GOALKEEPER(1),
    DEFENDER(2),
    MIDFIELDER(3),
    ATTACKER(4),
    UNKNOWN(0);

    companion object {
        fun fromId(id: Int): PlayerPosition =
            entries.firstOrNull { it.positionId == id } ?: UNKNOWN
    }
}

data class LeagueStanding(
    val position: Int,
    val team: Team,
    val played: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val points: Int,
    val goalDifference: Int = 0,
    val trend: String = "up"
)

data class Video(
    val id: String,
    val title: String,
    val thumbnailUrl: String,
    val videoUrl: String,
    val category: String,
    val date: String,
    val duration: String
)
