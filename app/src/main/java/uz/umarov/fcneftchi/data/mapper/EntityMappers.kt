package uz.umarov.fcneftchi.data.mapper

import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.data.local.entity.LeagueStandingEntity
import uz.umarov.fcneftchi.data.local.entity.MatchEntity
import uz.umarov.fcneftchi.data.local.entity.NewsArticleEntity
import uz.umarov.fcneftchi.data.local.entity.PlayerEntity
import uz.umarov.fcneftchi.data.model.LeagueStanding
import uz.umarov.fcneftchi.data.model.Match
import uz.umarov.fcneftchi.data.model.MatchStatus
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.data.model.Player
import uz.umarov.fcneftchi.data.model.Team
import uz.umarov.fcneftchi.util.DateUtils
import java.util.Date

fun NewsArticle.toEntity(sortOrder: Int, cachedAt: Long): NewsArticleEntity = NewsArticleEntity(
    id = id,
    title = title,
    imageUrl = imageUrl,
    date = date,
    content = content,
    description = description,
    category = category,
    url = url,
    sortOrder = sortOrder,
    cachedAt = cachedAt
)

fun NewsArticleEntity.toArticle(): NewsArticle = NewsArticle(
    id = id,
    title = title,
    imageUrl = imageUrl,
    date = date,
    content = content,
    description = description,
    category = category,
    url = url
)

fun Match.toEntity(cachedAt: Long): MatchEntity = MatchEntity(
    id = id,
    homeTeamId = homeTeam.id,
    homeTeamName = homeTeam.name,
    homeTeamLogo = homeTeam.logoUrl,
    awayTeamId = awayTeam.id,
    awayTeamName = awayTeam.name,
    awayTeamLogo = awayTeam.logoUrl,
    homeScore = homeScore,
    awayScore = awayScore,
    matchDate = matchDate,
    competition = competition,
    cachedAt = cachedAt
)

fun MatchEntity.toMatch(): Match {
    val status = if (DateUtils.parseDate(matchDate)?.after(Date()) == true) {
        MatchStatus.SCHEDULED
    } else {
        MatchStatus.FINISHED
    }
    return Match(
        id = id,
        homeTeam = Team(homeTeamId, homeTeamName, homeTeamLogo),
        awayTeam = Team(awayTeamId, awayTeamName, awayTeamLogo),
        homeScore = homeScore,
        awayScore = awayScore,
        matchDate = matchDate,
        status = status,
        competition = competition
    )
}

fun LeagueStanding.toEntity(seasonId: Int, cachedAt: Long): LeagueStandingEntity =
    LeagueStandingEntity(
        seasonId = seasonId,
        teamId = team.id,
        position = position,
        teamName = team.name,
        teamLogo = team.logoUrl,
        played = played,
        wins = wins,
        draws = draws,
        losses = losses,
        points = points,
        goalDifference = goalDifference,
        cachedAt = cachedAt
    )

fun LeagueStandingEntity.toLeagueStanding(): LeagueStanding = LeagueStanding(
    position = position,
    team = Team(teamId, teamName, teamLogo),
    played = played,
    wins = wins,
    draws = draws,
    losses = losses,
    points = points,
    goalDifference = goalDifference
)

fun Player.toEntity(cachedAt: Long): PlayerEntity = PlayerEntity(
    id = id,
    name = name,
    number = number,
    position = position,
    photoUrl = (imageUrl as? String),
    nationality = nationality,
    cachedAt = cachedAt
)

fun PlayerEntity.toPlayer(): Player = Player(
    id = id,
    name = name,
    number = number,
    position = position,
    imageUrl = photoUrl ?: R.drawable.player_placeholder_inset,
    nationality = nationality
)
