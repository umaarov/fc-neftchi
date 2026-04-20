package uz.umarov.fcneftchi.util

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Date

object DateUtils {

    private val ISO_UTC = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    private val ISO_UTC_MILLIS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private val ISO_LOCAL = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val SLASHED_LOCAL = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    fun parseDate(dateString: String?): Date? {
        if (dateString.isNullOrEmpty()) return null

        val utcParsers = listOf(ISO_UTC_MILLIS, ISO_UTC)
        for (parser in utcParsers) {
            runCatching {
                val local = LocalDateTime.parse(dateString, parser)
                return Date(local.toInstant(ZoneOffset.UTC).toEpochMilli())
            }.onFailure { if (it !is DateTimeParseException) throw it }
        }

        val localParsers = listOf(ISO_LOCAL, SLASHED_LOCAL)
        for (parser in localParsers) {
            runCatching {
                val local = LocalDateTime.parse(dateString, parser)
                val millis = local.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                return Date(millis)
            }.onFailure { if (it !is DateTimeParseException) throw it }
        }

        runCatching {
            return Date(OffsetDateTime.parse(dateString).toInstant().toEpochMilli())
        }.onFailure { if (it !is DateTimeParseException) throw it }

        return null
    }
}
