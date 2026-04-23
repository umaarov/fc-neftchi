package uz.umarov.fcneftchi.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationChannelRegistrar @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun registerAll() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService<NotificationManager>() ?: return
        NotificationCategory.entries.forEach { category ->
            val channel = NotificationChannel(
                category.channelId,
                context.getString(category.titleRes),
                category.importance,
            ).apply {
                description = context.getString(category.descriptionRes)
            }
            manager.createNotificationChannel(channel)
        }
    }
}
