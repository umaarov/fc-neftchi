package uz.umarov.fcneftchi.data.mapper

import uz.umarov.fcneftchi.data.model.GameCalendarMatch
import uz.umarov.fcneftchi.data.model.Match
import uz.umarov.fcneftchi.data.model.MatchStatus
import uz.umarov.fcneftchi.data.model.Team
import uz.umarov.fcneftchi.util.DateUtils
import java.util.Date

fun GameCalendarMatch.toMatch(): Match {
    val homeTeam = Team(
        id = homeTeam.club.id,
        name = homeTeam.club.title,
        logoUrl = homeTeam.club.logo
    )
    val awayTeam = Team(
        id = this.awayTeam.club.id,
        name = this.awayTeam.club.title,
        logoUrl = this.awayTeam.club.logo
    )
    val status = if (DateUtils.parseDate(startDate)?.after(Date()) == true) {
        MatchStatus.SCHEDULED
    } else {
        MatchStatus.FINISHED
    }

    return Match(
        id = id.toString(),
        homeTeam = homeTeam,
        awayTeam = awayTeam,
        homeScore = homeGoal,
        awayScore = awayGoal,
        matchDate = startDate,
        status = status,
        competition = ""
    )
}
