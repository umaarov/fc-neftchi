package uz.umarov.fcneftchi.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.umarov.fcneftchi.data.repository.MockNeftchiRepository
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNeftchiRepository(): NeftchiRepository {
        return MockNeftchiRepository()
    }
}

