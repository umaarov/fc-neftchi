package uz.umarov.fcneftchi

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import uz.umarov.fcneftchi.util.ThemeManager
import javax.inject.Inject
import javax.inject.Provider

@HiltAndroidApp
class NeftchiApp : Application(), ImageLoaderFactory {

    @Inject
    lateinit var imageLoaderProvider: Provider<ImageLoader.Builder>

    @Inject
    lateinit var themeManager: ThemeManager

    override fun onCreate() {
        super.onCreate()
        // Apply user's persisted theme before any activity inflates to avoid
        // a flash of the wrong light/dark mode on cold start.
        themeManager.applyPersisted()

        FirebaseCrashlytics.getInstance()
            .isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG
    }

    override fun newImageLoader(): ImageLoader {
        return imageLoaderProvider.get()
            .build()
    }
}
