package uz.umarov.fcneftchi.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import uz.umarov.fcneftchi.data.ClubConfig
import uz.umarov.fcneftchi.data.api.PflApiService
import uz.umarov.fcneftchi.data.mapper.toNewsArticle
import uz.umarov.fcneftchi.data.model.NewsArticle
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val apiService: PflApiService
) : NewsRepository {

    override fun getNews(): Flow<List<NewsArticle>> = flow {
        val response = apiService.getNews(ClubConfig.CLUB_ID)
        emit(response.data.list.map { it.toNewsArticle() })
    }

    override fun getNewsArticleByUrl(url: String): Flow<NewsArticle?> = flow {
        try {
            val response = apiService.getNewsDetail(url)
            Timber.tag("NewsRepo").d("Fetched News Detail: %s", response.data)
            emit(response.data.toNewsArticle(url))
        } catch (e: Exception) {
            Timber.tag("NewsRepo").e(e, "Error fetching news article by URL: %s", url)
            emit(null)
        }
    }
}
