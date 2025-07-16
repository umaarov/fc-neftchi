package uz.umarov.fcneftchi.util

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefsHelper @Inject constructor(private val prefs: SharedPreferences) {

    companion object {
        const val PREF_NOTIF_MATCH_REMINDER = "pref_notif_match_reminder"
        const val PREF_NOTIF_NEWS_ALERTS = "pref_notif_news_alerts"
    }

    fun setBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean = true): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }
}