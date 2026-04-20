package uz.umarov.fcneftchi.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.model.StatisticsData
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import javax.inject.Inject

class GetClubStatisticsUseCase @Inject constructor(
    private val repository: NeftchiRepository
) {
    operator fun invoke(): Flow<StatisticsData?> = repository.getClubStatistics()
}
