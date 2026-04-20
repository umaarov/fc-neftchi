package uz.umarov.fcneftchi.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.model.Match
import uz.umarov.fcneftchi.data.repository.MatchRepository
import javax.inject.Inject

class GetNextMatchUseCase @Inject constructor(
    private val matchRepository: MatchRepository
) {
    operator fun invoke(): Flow<Match?> = matchRepository.getNextMatch()
}
