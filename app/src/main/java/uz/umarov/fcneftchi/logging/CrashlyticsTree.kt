package uz.umarov.fcneftchi.logging

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class CrashlyticsTree : Timber.Tree() {

    private val crashlytics: FirebaseCrashlytics by lazy { FirebaseCrashlytics.getInstance() }

    override fun isLoggable(tag: String?, priority: Int): Boolean {
        return priority >= Log.WARN
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        crashlytics.log(if (tag != null) "[$tag] $message" else message)
        if (priority >= Log.ERROR && t != null) {
            crashlytics.recordException(t)
        }
    }
}
