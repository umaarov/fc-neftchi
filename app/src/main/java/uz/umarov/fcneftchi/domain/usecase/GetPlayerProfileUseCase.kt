package uz.umarov.fcneftchi.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.model.PlayerProfile
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import javax.inject.Inject

class GetPlayerProfileUseCase @Inject constructor(
    private val repository: NeftchiRepository
) {
    operator fun invoke(playerId: Int): Flow<PlayerProfile?> =
        repository.getPlayerProfile(playerId)
}
