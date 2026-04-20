package uz.umarov.fcneftchi.util

import androidx.appcompat.app.AppCompatDelegate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Applies the user's chosen theme and persists it across launches.
 *
 * Apply once from [uz.umarov.fcneftchi.NeftchiApp.onCreate] so the first
 * activity inflates with the right theme (no flash of the wrong mode).
 */
@Singleton
class ThemeManager @Inject constructor(
    private val prefs: SharedPrefsHelper
) {

    enum class Theme(val nightMode: Int) {
        LIGHT(AppCompatDelegate.MODE_NIGHT_NO),
        DARK(AppCompatDelegate.MODE_NIGHT_YES),
        SYSTEM(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        companion object {
            fun fromKey(key: String?): Theme =
                entries.firstOrNull { it.name == key } ?: SYSTEM
        }
    }

    fun currentTheme(): Theme = Theme.fromKey(prefs.getString(SharedPrefsHelper.PREF_THEME))

    fun apply(theme: Theme) {
        prefs.setString(SharedPrefsHelper.PREF_THEME, theme.name)
        AppCompatDelegate.setDefaultNightMode(theme.nightMode)
    }

    /** Call on app start to restore the persisted theme. */
    fun applyPersisted() {
        AppCompatDelegate.setDefaultNightMode(currentTheme().nightMode)
    }
}
