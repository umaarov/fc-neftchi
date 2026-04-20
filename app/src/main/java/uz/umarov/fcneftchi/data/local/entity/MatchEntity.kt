package uz.umarov.fcneftchi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey val id: String,
    val homeTeamId: Int,
    val homeTeamName: String,
    val homeTeamLogo: String,
    val awayTeamId: Int,
    val awayTeamName: String,
    val awayTeamLogo: String,
    val homeScore: Int?,
    val awayScore: Int?,
    val matchDate: String,
    val competition: String,
    val cachedAt: Long
)
