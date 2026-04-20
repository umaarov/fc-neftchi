package uz.umarov.fcneftchi.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.model.Player
import uz.umarov.fcneftchi.data.repository.PlayerRepository
import javax.inject.Inject

class GetSquadUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    operator fun invoke(): Flow<List<Player>> = playerRepository.getSquad()
}
