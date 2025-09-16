package uz.umarov.fcneftchi.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import coil.ImageLoader
import coil.decode.SvgDecoder
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import uz.umarov.fcneftchi.data.api.PflApiService
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import uz.umarov.fcneftchi.data.repository.RealNeftchiRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val builder = OkHttpClient.Builder()
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(PflApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun providePflApiService(retrofit: Retrofit): PflApiService {
        return retrofit.create(PflApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNeftchiRepository(apiService: PflApiService): NeftchiRepository {
        return RealNeftchiRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideImageLoaderBuilder(app: Application): ImageLoader.Builder {
        return ImageLoader.Builder(app)
            .components {
                add(SvgDecoder.Factory())
            }
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(app: Application): SharedPreferences {
        return app.getSharedPreferences("neftchi_prefs", Context.MODE_PRIVATE)
    }
}