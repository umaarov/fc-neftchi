package uz.umarov.fcneftchi.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.ui.MainActivity
import kotlin.random.Random

class NeftchiFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.data["title"] ?: remoteMessage.notification?.title ?: "FC Neftchi"
        val body = remoteMessage.data["body"] ?: remoteMessage.notification?.body ?: "Yangi xabar"
        val articleUrl = remoteMessage.data["articleUrl"]

        sendNotification(title, body, articleUrl)
    }

    private fun sendNotification(title: String, messageBody: String, articleUrl: String?) {
        val channelId = "neftchi_news_channel"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Neftchi Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Yangi yangiliklar va o'yin eslatmalari"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_neftchi)
            .setColor(ContextCompat.getColor(this, R.color.neftchi_green))
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(Random.nextInt(), notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.tag("FCM_TOKEN").d("New device token: %s", token)
    }
}