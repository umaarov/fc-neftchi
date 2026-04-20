package uz.umarov.fcneftchi.util

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefsHelper @Inject constructor(private val prefs: SharedPreferences) {

    companion object {
        const val PREF_NOTIF_MATCH_REMINDER = "pref_notif_match_reminder"
        const val PREF_NOTIF_NEWS_ALERTS = "pref_notif_news_alerts"
        const val PREF_THEME = "pref_theme"
        const val PREF_LANGUAGE = "pref_language"
    }

    fun setBoolean(key: String, value: Boolean) {
        prefs.edit { putBoolean(key, value) }
    }

    fun getBoolean(key: String, defaultValue: Boolean = true): Boolean =
        prefs.getBoolean(key, defaultValue)

    fun setString(key: String, value: String?) {
        prefs.edit { putString(key, value) }
    }

    fun getString(key: String, defaultValue: String? = null): String? =
        prefs.getString(key, defaultValue)
}
