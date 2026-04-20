package uz.umarov.fcneftchi.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Analytics @Inject constructor(
    @ApplicationContext context: Context
) {

    private val firebase: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    fun logScreen(screenName: String) {
        firebase.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        })
    }

    fun logNewsOpened(articleUrl: String) {
        firebase.logEvent(EVENT_NEWS_OPENED, Bundle().apply {
            putString(PARAM_ARTICLE_URL, articleUrl)
        })
    }

    fun logMatchOpened(matchId: Int) {
        firebase.logEvent(EVENT_MATCH_OPENED, Bundle().apply {
            putInt(PARAM_MATCH_ID, matchId)
        })
    }

    fun logVideoPlayed(videoId: String) {
        firebase.logEvent(EVENT_VIDEO_PLAYED, Bundle().apply {
            putString(PARAM_VIDEO_ID, videoId)
        })
    }

    fun logPlayerOpened(playerId: Int) {
        firebase.logEvent(EVENT_PLAYER_OPENED, Bundle().apply {
            putInt(PARAM_PLAYER_ID, playerId)
        })
    }

    companion object {
        private const val EVENT_NEWS_OPENED = "news_opened"
        private const val EVENT_MATCH_OPENED = "match_opened"
        private const val EVENT_VIDEO_PLAYED = "video_played"
        private const val EVENT_PLAYER_OPENED = "player_opened"

        private const val PARAM_ARTICLE_URL = "article_url"
        private const val PARAM_MATCH_ID = "match_id"
        private const val PARAM_VIDEO_ID = "video_id"
        private const val PARAM_PLAYER_ID = "player_id"
    }
}
