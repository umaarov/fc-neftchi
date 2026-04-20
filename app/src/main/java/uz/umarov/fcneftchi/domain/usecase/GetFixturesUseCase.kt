package uz.umarov.fcneftchi.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.model.Match
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import javax.inject.Inject

class GetFixturesUseCase @Inject constructor(
    private val repository: NeftchiRepository
) {
    operator fun invoke(): Flow<List<Match>> = repository.getAllFixtures()
}
