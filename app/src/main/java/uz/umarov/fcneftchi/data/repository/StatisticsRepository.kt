package uz.umarov.fcneftchi.data.repository

import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.model.StatisticsData

interface StatisticsRepository {
    fun getClubStatistics(): Flow<StatisticsData?>
}
