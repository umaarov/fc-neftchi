package uz.umarov.fcneftchi.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.model.LeagueStanding
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import javax.inject.Inject

class GetLeagueTableUseCase @Inject constructor(
    private val repository: NeftchiRepository
) {
    operator fun invoke(seasonId: Int): Flow<List<LeagueStanding>> =
        repository.getLeagueTable(seasonId)
}
