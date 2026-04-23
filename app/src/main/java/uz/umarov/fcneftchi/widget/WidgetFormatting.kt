package uz.umarov.fcneftchi.widget

import android.content.Context
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.data.model.Match
import uz.umarov.fcneftchi.util.DateUtils

object WidgetFormatting {

    fun countdown(context: Context, match: Match): String {
        val target = DateUtils.parseDate(match.matchDate)?.time
            ?: return context.getString(R.string.value_unavailable)
        val diff = target - System.currentTimeMillis()
        if (diff <= 0) return context.getString(R.string.widget_kickoff_now)

        val totalMinutes = diff / 60_000
        val days = totalMinutes / (60 * 24)
        val hours = (totalMinutes / 60) % 24
        val minutes = totalMinutes % 60
        return when {
            days > 0 -> context.getString(
                R.string.widget_countdown_days_format,
                days, hours, minutes
            )
            hours > 0 -> context.getString(
                R.string.widget_countdown_hours_format,
                hours, minutes
            )
            else -> context.getString(
                R.string.widget_countdown_minutes_format,
                minutes
            )
        }
    }

    fun lastResult(context: Context, match: Match): String {
        val home = match.homeTeam.name
        val away = match.awayTeam.name
        val homeScore = match.homeScore ?: 0
        val awayScore = match.awayScore ?: 0
        return context.getString(
            R.string.widget_last_result_format,
            home, homeScore, awayScore, away
        )
    }
}
