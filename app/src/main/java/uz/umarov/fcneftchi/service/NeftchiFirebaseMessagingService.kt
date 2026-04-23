package uz.umarov.fcneftchi.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.notifications.NotificationCategory
import uz.umarov.fcneftchi.notifications.NotificationChannelRegistrar
import uz.umarov.fcneftchi.ui.MainActivity
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class NeftchiFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var channelRegistrar: NotificationChannelRegistrar

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.data["title"]
            ?: remoteMessage.notification?.title
            ?: getString(R.string.notif_default_title)
        val body = remoteMessage.data["body"]
            ?: remoteMessage.notification?.body
            ?: getString(R.string.notif_default_body)
        val articleUrl = remoteMessage.data["articleUrl"]
        val category = NotificationCategory.fromMessageData(remoteMessage.data["category"])

        sendNotification(category, title, body, articleUrl)
    }

    private fun sendNotification(
        category: NotificationCategory,
        title: String,
        messageBody: String,
        articleUrl: String?,
    ) {
        channelRegistrar.registerAll()

        val pendingIntent: PendingIntent = if (!articleUrl.isNullOrBlank()) {
            NavDeepLinkBuilder(this)
                .setComponentName(MainActivity::class.java)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.newsArticleFragment)
                .setArguments(Bundle().apply {
                    putString("articleUrl", articleUrl)
                })
                .createPendingIntent()
        } else {
            val defaultIntent = Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            PendingIntent.getActivity(
                this, 0, defaultIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        val priority = when (category) {
            NotificationCategory.GOALS,
            NotificationCategory.KICKOFF -> NotificationCompat.PRIORITY_HIGH
            NotificationCategory.NEWS,
            NotificationCategory.SIGNINGS -> NotificationCompat.PRIORITY_DEFAULT
        }

        val notification = NotificationCompat.Builder(this, category.channelId)
            .setSmallIcon(R.drawable.ic_stat_neftchi)
            .setColor(ContextCompat.getColor(this, R.color.neftchi_green))
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(priority)
            .build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Random.nextInt(), notification)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.tag("FCM_TOKEN").d("New device token: %s", token)
    }
}
