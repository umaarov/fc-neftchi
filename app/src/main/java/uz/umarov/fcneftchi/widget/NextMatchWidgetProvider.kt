package uz.umarov.fcneftchi.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.data.model.Match
import uz.umarov.fcneftchi.ui.SplashActivity
import java.util.concurrent.TimeUnit

class NextMatchWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        // Render whatever the last worker cached; fetch fresh in the background.
        appWidgetIds.forEach { id ->
            appWidgetManager.updateAppWidget(id, buildInitialViews(context))
        }
        WorkManager.getInstance(context).enqueue(
            OneTimeWorkRequestBuilder<NextMatchWidgetWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
        )
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        schedulePeriodicRefresh(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        WorkManager.getInstance(context).cancelUniqueWork(PERIODIC_WORK_NAME)
    }

    private fun buildInitialViews(context: Context): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_next_match)
        views.setOnClickPendingIntent(R.id.widget_root, launchAppIntent(context))
        return views
    }

    companion object {
        private const val PERIODIC_WORK_NAME = "next_match_widget_refresh"

        fun schedulePeriodicRefresh(context: Context) {
            val request = PeriodicWorkRequestBuilder<NextMatchWidgetWorker>(
                30, TimeUnit.MINUTES
            ).setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                PERIODIC_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        fun renderWidget(context: Context, nextMatch: Match?, lastMatch: Match?) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                ComponentName(context, NextMatchWidgetProvider::class.java)
            )
            if (ids.isEmpty()) return
            ids.forEach { id ->
                val views = RemoteViews(context.packageName, R.layout.widget_next_match)
                bindNextMatch(context, views, nextMatch)
                bindLastMatch(context, views, lastMatch)
                views.setOnClickPendingIntent(R.id.widget_root, launchAppIntent(context))
                manager.updateAppWidget(id, views)
            }
        }

        private fun bindNextMatch(context: Context, views: RemoteViews, match: Match?) {
            if (match == null) {
                views.setTextViewText(
                    R.id.widget_next_matchup,
                    context.getString(R.string.widget_next_match_none)
                )
                views.setTextViewText(R.id.widget_next_countdown, "")
                return
            }
            val matchup = context.getString(
                R.string.widget_matchup_format,
                match.homeTeam.name,
                match.awayTeam.name
            )
            views.setTextViewText(R.id.widget_next_matchup, matchup)
            views.setTextViewText(R.id.widget_next_countdown, WidgetFormatting.countdown(context, match))
        }

        private fun bindLastMatch(context: Context, views: RemoteViews, match: Match?) {
            if (match == null) {
                views.setTextViewText(
                    R.id.widget_last_result,
                    context.getString(R.string.widget_last_result_none)
                )
                return
            }
            views.setTextViewText(
                R.id.widget_last_result,
                WidgetFormatting.lastResult(context, match)
            )
        }

        private fun launchAppIntent(context: Context): PendingIntent {
            val intent = Intent(context, SplashActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            return PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }
}
