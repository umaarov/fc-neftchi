package uz.umarov.fcneftchi.data.repository

import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.data.model.Player
import uz.umarov.fcneftchi.data.model.Video

interface BookmarkRepository {
    fun observeArticles(): Flow<List<NewsArticle>>
    fun observeVideos(): Flow<List<Video>>
    fun observePlayers(): Flow<List<Player>>

    fun isArticleBookmarked(url: String): Flow<Boolean>
    fun isVideoBookmarked(id: String): Flow<Boolean>
    fun isPlayerBookmarked(id: Int): Flow<Boolean>

    suspend fun toggleArticle(article: NewsArticle)
    suspend fun toggleVideo(video: Video)
    suspend fun togglePlayer(player: Player)
}
