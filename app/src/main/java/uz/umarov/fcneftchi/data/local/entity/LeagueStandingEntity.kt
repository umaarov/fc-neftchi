package uz.umarov.fcneftchi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "league_standings", primaryKeys = ["seasonId", "teamId"])
data class LeagueStandingEntity(
    val seasonId: Int,
    val teamId: Int,
    val position: Int,
    val teamName: String,
    val teamLogo: String,
    val played: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val points: Int,
    val goalDifference: Int,
    val cachedAt: Long
)
