package uz.umarov.fcneftchi.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.data.repository.NewsRepository
import javax.inject.Inject

class GetNewsDetailUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    operator fun invoke(url: String): Flow<NewsArticle?> =
        newsRepository.getNewsArticleByUrl(url)
}
