package uz.umarov.fcneftchi.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.model.LeagueStanding
import uz.umarov.fcneftchi.data.repository.StandingsRepository
import javax.inject.Inject

class GetLeagueTableUseCase @Inject constructor(
    private val standingsRepository: StandingsRepository
) {
    operator fun invoke(seasonId: Int): Flow<List<LeagueStanding>> =
        standingsRepository.getLeagueTable(seasonId)
}
