package uz.umarov.fcneftchi.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.model.Match
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import javax.inject.Inject

class GetNextMatchUseCase @Inject constructor(
    private val repository: NeftchiRepository
) {
    operator fun invoke(): Flow<Match?> = repository.getNextMatch()
}
