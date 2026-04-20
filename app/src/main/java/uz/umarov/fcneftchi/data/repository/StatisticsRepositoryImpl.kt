package uz.umarov.fcneftchi.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.umarov.fcneftchi.data.ClubConfig
import uz.umarov.fcneftchi.data.api.PflApiService
import uz.umarov.fcneftchi.data.model.StatisticsData
import javax.inject.Inject

class StatisticsRepositoryImpl @Inject constructor(
    private val apiService: PflApiService
) : StatisticsRepository {

    override fun getClubStatistics(): Flow<StatisticsData?> = flow {
        try {
            emit(apiService.getClubStatistics(ClubConfig.CLUB_ID).data)
        } catch (e: Exception) {
            emit(null)
        }
    }
}
