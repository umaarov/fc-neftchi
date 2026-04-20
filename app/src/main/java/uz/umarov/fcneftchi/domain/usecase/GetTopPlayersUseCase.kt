package uz.umarov.fcneftchi.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.model.TopPlayer
import uz.umarov.fcneftchi.data.repository.StandingsRepository
import javax.inject.Inject

class GetTopPlayersUseCase @Inject constructor(
    private val standingsRepository: StandingsRepository
) {
    operator fun invoke(): Flow<List<TopPlayer>> = standingsRepository.getTopPlayers()
}
