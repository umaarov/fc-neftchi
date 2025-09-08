package uz.umarov.fcneftchi.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    @SuppressLint("ConstantLocale")
    private val SUPPORTED_FORMATS = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()),
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    )

    fun parseDate(dateString: String?): Date? {
        if (dateString.isNullOrEmpty()) {
            return null
        }
        for (format in SUPPORTED_FORMATS) {
            try {
                return format.parse(dateString)
            } catch (e: Exception) {
            }
        }
        return null
    }
}