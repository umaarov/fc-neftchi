package uz.umarov.fcneftchi.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import uz.umarov.fcneftchi.data.local.dao.BookmarkArticleDao
import uz.umarov.fcneftchi.data.local.dao.BookmarkPlayerDao
import uz.umarov.fcneftchi.data.local.dao.BookmarkVideoDao
import uz.umarov.fcneftchi.data.mapper.toArticle
import uz.umarov.fcneftchi.data.mapper.toBookmarkEntity
import uz.umarov.fcneftchi.data.mapper.toPlayer
import uz.umarov.fcneftchi.data.mapper.toVideo
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.data.model.Player
import uz.umarov.fcneftchi.data.model.Video
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val articleDao: BookmarkArticleDao,
    private val videoDao: BookmarkVideoDao,
    private val playerDao: BookmarkPlayerDao,
) : BookmarkRepository {

    override fun observeArticles(): Flow<List<NewsArticle>> =
        articleDao.observeAll().map { list -> list.map { it.toArticle() } }

    override fun observeVideos(): Flow<List<Video>> =
        videoDao.observeAll().map { list -> list.map { it.toVideo() } }

    override fun observePlayers(): Flow<List<Player>> =
        playerDao.observeAll().map { list -> list.map { it.toPlayer() } }

    override fun isArticleBookmarked(url: String): Flow<Boolean> =
        articleDao.observeIsBookmarked(url)

    override fun isVideoBookmarked(id: String): Flow<Boolean> =
        videoDao.observeIsBookmarked(id)

    override fun isPlayerBookmarked(id: Int): Flow<Boolean> =
        playerDao.observeIsBookmarked(id)

    override suspend fun toggleArticle(article: NewsArticle) {
        val bookmarked = articleDao.observeIsBookmarked(article.url).first()
        if (bookmarked) {
            articleDao.remove(article.url)
        } else {
            articleDao.upsert(article.toBookmarkEntity(System.currentTimeMillis()))
        }
    }

    override suspend fun toggleVideo(video: Video) {
        val bookmarked = videoDao.observeIsBookmarked(video.id).first()
        if (bookmarked) {
            videoDao.remove(video.id)
        } else {
            videoDao.upsert(video.toBookmarkEntity(System.currentTimeMillis()))
        }
    }

    override suspend fun togglePlayer(player: Player) {
        val bookmarked = playerDao.observeIsBookmarked(player.id).first()
        if (bookmarked) {
            playerDao.remove(player.id)
        } else {
            playerDao.upsert(player.toBookmarkEntity(System.currentTimeMillis()))
        }
    }
}
