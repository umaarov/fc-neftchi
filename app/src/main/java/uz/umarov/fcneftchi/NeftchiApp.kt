package uz.umarov.fcneftchi

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import uz.umarov.fcneftchi.logging.CrashlyticsTree
import uz.umarov.fcneftchi.notifications.NotificationChannelRegistrar
import uz.umarov.fcneftchi.util.ThemeManager
import uz.umarov.fcneftchi.widget.NextMatchWidgetProvider
import javax.inject.Inject
import javax.inject.Provider

@HiltAndroidApp
class NeftchiApp : Application(), ImageLoaderFactory, Configuration.Provider {

    @Inject
    lateinit var imageLoaderProvider: Provider<ImageLoader.Builder>

    @Inject
    lateinit var themeManager: ThemeManager

    @Inject
    lateinit var notificationChannelRegistrar: NotificationChannelRegistrar

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        // Apply user's persisted theme before any activity inflates to avoid
        // a flash of the wrong light/dark mode on cold start.
        themeManager.applyPersisted()

        notificationChannelRegistrar.registerAll()

        FirebaseCrashlytics.getInstance()
            .isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsTree())
        }

        NextMatchWidgetProvider.schedulePeriodicRefresh(this)
    }

    override fun newImageLoader(): ImageLoader {
        return imageLoaderProvider.get()
            .build()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) android.util.Log.INFO else android.util.Log.ERROR)
            .build()
}
