package uz.umarov.fcneftchi.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.model.PlayerProfile
import uz.umarov.fcneftchi.data.repository.PlayerRepository
import javax.inject.Inject

class GetPlayerProfileUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    operator fun invoke(playerId: Int): Flow<PlayerProfile?> =
        playerRepository.getPlayerProfile(playerId)
}
