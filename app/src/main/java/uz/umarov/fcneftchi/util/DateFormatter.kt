package uz.umarov.fcneftchi.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object DateFormatter {

    private val zone: ZoneId get() = ZoneId.systemDefault()

    private val articleDate = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())
    private val matchListDate = DateTimeFormatter.ofPattern("E d MMM yyyy", Locale.getDefault())
    private val matchListTime = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    private val matchDetailDate =
        DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm", Locale.getDefault())
    private val monthHeader = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
    private val shortEnglish = DateTimeFormatter.ofPattern("dd MMM, HH:mm", Locale.ENGLISH)

    fun formatArticleDate(date: Date): String = date.format(articleDate)
    fun formatArticleDateOrRaw(raw: String): String =
        DateUtils.parseDate(raw)?.let { formatArticleDate(it) } ?: raw

    fun formatMatchListDate(date: Date): String = date.format(matchListDate)
    fun formatMatchListTime(date: Date): String = date.format(matchListTime)
    fun formatMatchDetailDate(date: Date): String = date.format(matchDetailDate)
    fun formatMonthHeader(date: Date): String = date.format(monthHeader)
    fun formatHomeShort(date: Date): String = date.format(shortEnglish).uppercase(Locale.ROOT)

    private fun Date.format(formatter: DateTimeFormatter): String =
        formatter.format(Instant.ofEpochMilli(time).atZone(zone))
}
