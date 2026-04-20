package uz.umarov.fcneftchi.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.model.TopPlayer
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import javax.inject.Inject

class GetTopPlayersUseCase @Inject constructor(
    private val repository: NeftchiRepository
) {
    operator fun invoke(): Flow<List<TopPlayer>> = repository.getTopPlayers()
}
