package uz.umarov.fcneftchi

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp
import uz.umarov.fcneftchi.util.NativeSecurity
import javax.inject.Inject
import javax.inject.Provider

@HiltAndroidApp
class NeftchiApp : Application(), ImageLoaderFactory {

    @Inject
    lateinit var imageLoaderProvider: Provider<ImageLoader.Builder>

    override fun onCreate() {
        super.onCreate()
        NativeSecurity.checkAndExit()
    }

    override fun newImageLoader(): ImageLoader {
        return imageLoaderProvider.get()
            .build()
    }
}
