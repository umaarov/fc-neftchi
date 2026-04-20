package uz.umarov.fcneftchi.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.model.GameDetail
import uz.umarov.fcneftchi.data.repository.MatchRepository
import javax.inject.Inject

class GetGameDetailsUseCase @Inject constructor(
    private val matchRepository: MatchRepository
) {
    operator fun invoke(gameId: Int): Flow<GameDetail?> =
        matchRepository.getGameDetails(gameId)
}
