package uz.umarov.fcneftchi.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import javax.inject.Inject

class GetNewsFeedUseCase @Inject constructor(
    private val repository: NeftchiRepository
) {
    operator fun invoke(): Flow<List<NewsArticle>> = repository.getNews()
}
