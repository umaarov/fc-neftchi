package uz.umarov.fcneftchi.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.umarov.fcneftchi.data.repository.MatchRepository
import uz.umarov.fcneftchi.data.repository.MatchRepositoryImpl
import uz.umarov.fcneftchi.data.repository.NewsRepository
import uz.umarov.fcneftchi.data.repository.NewsRepositoryImpl
import uz.umarov.fcneftchi.data.repository.PlayerRepository
import uz.umarov.fcneftchi.data.repository.PlayerRepositoryImpl
import uz.umarov.fcneftchi.data.repository.StandingsRepository
import uz.umarov.fcneftchi.data.repository.StandingsRepositoryImpl
import uz.umarov.fcneftchi.data.repository.StatisticsRepository
import uz.umarov.fcneftchi.data.repository.StatisticsRepositoryImpl
import uz.umarov.fcneftchi.data.repository.VideoRepository
import uz.umarov.fcneftchi.data.repository.VideoRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNewsRepository(impl: NewsRepositoryImpl): NewsRepository

    @Binds
    @Singleton
    abstract fun bindMatchRepository(impl: MatchRepositoryImpl): MatchRepository

    @Binds
    @Singleton
    abstract fun bindPlayerRepository(impl: PlayerRepositoryImpl): PlayerRepository

    @Binds
    @Singleton
    abstract fun bindStandingsRepository(impl: StandingsRepositoryImpl): StandingsRepository

    @Binds
    @Singleton
    abstract fun bindVideoRepository(impl: VideoRepositoryImpl): VideoRepository

    @Binds
    @Singleton
    abstract fun bindStatisticsRepository(impl: StatisticsRepositoryImpl): StatisticsRepository
}
