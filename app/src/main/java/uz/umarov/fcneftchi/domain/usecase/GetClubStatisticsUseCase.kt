package uz.umarov.fcneftchi.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.model.StatisticsData
import uz.umarov.fcneftchi.data.repository.StatisticsRepository
import javax.inject.Inject

class GetClubStatisticsUseCase @Inject constructor(
    private val statisticsRepository: StatisticsRepository
) {
    operator fun invoke(): Flow<StatisticsData?> = statisticsRepository.getClubStatistics()
}
