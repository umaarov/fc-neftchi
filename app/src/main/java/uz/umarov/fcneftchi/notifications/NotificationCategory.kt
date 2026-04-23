package uz.umarov.fcneftchi.notifications

import android.app.NotificationManager
import androidx.annotation.StringRes
import uz.umarov.fcneftchi.R

enum class NotificationCategory(
    val id: String,
    val channelId: String,
    val topic: String,
    val prefKey: String,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    val importance: Int,
    val defaultEnabled: Boolean,
) {
    GOALS(
        id = "goals",
        channelId = "neftchi_channel_goals",
        topic = "goals",
        prefKey = "pref_notif_goals",
        titleRes = R.string.notif_category_goals,
        descriptionRes = R.string.notif_category_goals_subtitle,
        importance = NotificationManager.IMPORTANCE_HIGH,
        defaultEnabled = true,
    ),
    KICKOFF(
        id = "kickoff",
        channelId = "neftchi_channel_kickoff",
        topic = "kickoff",
        prefKey = "pref_notif_kickoff",
        titleRes = R.string.notif_category_kickoff,
        descriptionRes = R.string.notif_category_kickoff_subtitle,
        importance = NotificationManager.IMPORTANCE_HIGH,
        defaultEnabled = true,
    ),
    NEWS(
        id = "news",
        channelId = "neftchi_channel_news",
        topic = "news",
        prefKey = "pref_notif_news",
        titleRes = R.string.notif_category_news,
        descriptionRes = R.string.notif_category_news_subtitle,
        importance = NotificationManager.IMPORTANCE_DEFAULT,
        defaultEnabled = true,
    ),
    SIGNINGS(
        id = "signings",
        channelId = "neftchi_channel_signings",
        topic = "signings",
        prefKey = "pref_notif_signings",
        titleRes = R.string.notif_category_signings,
        descriptionRes = R.string.notif_category_signings_subtitle,
        importance = NotificationManager.IMPORTANCE_DEFAULT,
        defaultEnabled = true,
    );

    companion object {
        fun fromMessageData(categoryId: String?): NotificationCategory =
            entries.firstOrNull { it.id.equals(categoryId, ignoreCase = true) } ?: NEWS
    }
}
