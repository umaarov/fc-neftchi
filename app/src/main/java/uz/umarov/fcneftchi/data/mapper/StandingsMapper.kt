package uz.umarov.fcneftchi.data.mapper

import uz.umarov.fcneftchi.data.model.LeagueStanding
import uz.umarov.fcneftchi.data.model.LeagueTableItem
import uz.umarov.fcneftchi.data.model.Team

fun LeagueTableItem.toLeagueStanding(position: Int): LeagueStanding = LeagueStanding(
    position = position,
    team = Team(
        id = id,
        name = title,
        logoUrl = logo
    ),
    played = games,
    wins = wins,
    draws = draws,
    losses = losses,
    points = points,
    goalDifference = goalDifference
)
