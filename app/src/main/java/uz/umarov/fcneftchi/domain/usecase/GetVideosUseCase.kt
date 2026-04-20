package uz.umarov.fcneftchi.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.model.Video
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import javax.inject.Inject

class GetVideosUseCase @Inject constructor(
    private val repository: NeftchiRepository
) {
    operator fun invoke(): Flow<List<Video>> = repository.getVideos()
}
