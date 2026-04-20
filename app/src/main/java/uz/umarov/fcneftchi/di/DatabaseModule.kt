package uz.umarov.fcneftchi.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.umarov.fcneftchi.data.local.NeftchiDatabase
import uz.umarov.fcneftchi.data.local.dao.LeagueStandingDao
import uz.umarov.fcneftchi.data.local.dao.MatchDao
import uz.umarov.fcneftchi.data.local.dao.NewsArticleDao
import uz.umarov.fcneftchi.data.local.dao.PlayerDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): NeftchiDatabase =
        Room.databaseBuilder(app, NeftchiDatabase::class.java, "neftchi.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideNewsArticleDao(db: NeftchiDatabase): NewsArticleDao = db.newsArticleDao()

    @Provides
    fun provideMatchDao(db: NeftchiDatabase): MatchDao = db.matchDao()

    @Provides
    fun provideLeagueStandingDao(db: NeftchiDatabase): LeagueStandingDao = db.leagueStandingDao()

    @Provides
    fun providePlayerDao(db: NeftchiDatabase): PlayerDao = db.playerDao()
}
