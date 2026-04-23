package uz.umarov.fcneftchi.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.data.model.Player
import uz.umarov.fcneftchi.data.model.Video
import uz.umarov.fcneftchi.data.repository.BookmarkRepository
import javax.inject.Inject

class ObserveBookmarkedArticlesUseCase @Inject constructor(
    private val repository: BookmarkRepository
) {
    operator fun invoke(): Flow<List<NewsArticle>> = repository.observeArticles()
}

class ObserveBookmarkedVideosUseCase @Inject constructor(
    private val repository: BookmarkRepository
) {
    operator fun invoke(): Flow<List<Video>> = repository.observeVideos()
}

class ObserveBookmarkedPlayersUseCase @Inject constructor(
    private val repository: BookmarkRepository
) {
    operator fun invoke(): Flow<List<Player>> = repository.observePlayers()
}

class IsArticleBookmarkedUseCase @Inject constructor(
    private val repository: BookmarkRepository
) {
    operator fun invoke(url: String): Flow<Boolean> = repository.isArticleBookmarked(url)
}

class IsVideoBookmarkedUseCase @Inject constructor(
    private val repository: BookmarkRepository
) {
    operator fun invoke(id: String): Flow<Boolean> = repository.isVideoBookmarked(id)
}

class IsPlayerBookmarkedUseCase @Inject constructor(
    private val repository: BookmarkRepository
) {
    operator fun invoke(id: Int): Flow<Boolean> = repository.isPlayerBookmarked(id)
}

class ToggleArticleBookmarkUseCase @Inject constructor(
    private val repository: BookmarkRepository
) {
    suspend operator fun invoke(article: NewsArticle) = repository.toggleArticle(article)
}

class ToggleVideoBookmarkUseCase @Inject constructor(
    private val repository: BookmarkRepository
) {
    suspend operator fun invoke(video: Video) = repository.toggleVideo(video)
}

class TogglePlayerBookmarkUseCase @Inject constructor(
    private val repository: BookmarkRepository
) {
    suspend operator fun invoke(player: Player) = repository.togglePlayer(player)
}
