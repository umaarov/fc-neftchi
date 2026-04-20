package uz.umarov.fcneftchi.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import uz.umarov.fcneftchi.data.ClubConfig
import uz.umarov.fcneftchi.data.api.PflApiService
import uz.umarov.fcneftchi.data.local.dao.NewsArticleDao
import uz.umarov.fcneftchi.data.mapper.toArticle
import uz.umarov.fcneftchi.data.mapper.toEntity
import uz.umarov.fcneftchi.data.mapper.toNewsArticle
import uz.umarov.fcneftchi.data.model.NewsArticle
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val apiService: PflApiService,
    private val dao: NewsArticleDao
) : NewsRepository {

    override fun getNews(): Flow<List<NewsArticle>> = flow {
        val cached = dao.observeAll().first().map { it.toArticle() }
        if (cached.isNotEmpty()) emit(cached)
        try {
            val fresh = apiService.getNews(ClubConfig.CLUB_ID)
                .data.list.map { it.toNewsArticle() }
            val now = System.currentTimeMillis()
            dao.replaceAll(fresh.mapIndexed { index, article -> article.toEntity(index, now) })
            emit(fresh)
        } catch (e: Exception) {
            Timber.tag("NewsRepo").e(e, "News refresh failed")
            if (cached.isEmpty()) throw e
        }
    }

    override fun getNewsArticleByUrl(url: String): Flow<NewsArticle?> = flow {
        val existing = dao.observeByUrl(url).first()
        val cached = existing?.toArticle()
        if (cached != null) emit(cached)
        try {
            val response = apiService.getNewsDetail(url)
            Timber.tag("NewsRepo").d("Fetched News Detail: %s", response.data)
            val fresh = response.data.toNewsArticle(url)
            val now = System.currentTimeMillis()
            val sortOrder = existing?.sortOrder ?: Int.MAX_VALUE
            dao.upsert(fresh.toEntity(sortOrder = sortOrder, cachedAt = now))
            emit(fresh)
        } catch (e: Exception) {
            Timber.tag("NewsRepo").e(e, "Error fetching news article by URL: %s", url)
            if (cached == null) emit(null)
        }
    }
}
