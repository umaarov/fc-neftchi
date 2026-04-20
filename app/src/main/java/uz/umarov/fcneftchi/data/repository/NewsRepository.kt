package uz.umarov.fcneftchi.data.repository

import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.model.NewsArticle

interface NewsRepository {
    fun getNews(): Flow<List<NewsArticle>>
    fun getNewsArticleByUrl(url: String): Flow<NewsArticle?>
}
