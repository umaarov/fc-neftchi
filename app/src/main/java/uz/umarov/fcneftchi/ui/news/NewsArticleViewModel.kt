package uz.umarov.fcneftchi.ui.news

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.domain.usecase.GetNewsDetailUseCase
import uz.umarov.fcneftchi.domain.usecase.IsArticleBookmarkedUseCase
import uz.umarov.fcneftchi.domain.usecase.ToggleArticleBookmarkUseCase
import javax.inject.Inject

data class NewsArticleState(
    val article: NewsArticle? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isBookmarked: Boolean = false,
)

sealed interface NewsArticleEvent {
    object BookmarkAdded : NewsArticleEvent
    object BookmarkRemoved : NewsArticleEvent
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NewsArticleViewModel @Inject constructor(
    private val getNewsDetail: GetNewsDetailUseCase,
    private val isArticleBookmarked: IsArticleBookmarkedUseCase,
    private val toggleArticleBookmark: ToggleArticleBookmarkUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val articleUrl: String = savedStateHandle.get<String>("articleUrl")!!
    private val retryTrigger = MutableStateFlow(0)
    private val events = MutableStateFlow<NewsArticleEvent?>(null)

    val uiState: StateFlow<NewsArticleState> = retryTrigger
        .flatMapLatest {
            val articleFlow = getNewsDetail(articleUrl)
                .map<NewsArticle?, NewsArticleState> { article ->
                    NewsArticleState(article = article, isLoading = false)
                }
                .onStart { emit(NewsArticleState(isLoading = true)) }
                .catch { e -> emit(NewsArticleState(isLoading = false, error = e.message)) }
            combine(articleFlow, isArticleBookmarked(articleUrl)) { state, bookmarked ->
                state.copy(isBookmarked = bookmarked)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NewsArticleState())

    val event: StateFlow<NewsArticleEvent?> = events

    fun retry() {
        retryTrigger.value += 1
    }

    fun toggleBookmark() {
        val article = uiState.value.article ?: return
        val wasBookmarked = uiState.value.isBookmarked
        viewModelScope.launch {
            toggleArticleBookmark(article)
            events.value = if (wasBookmarked) {
                NewsArticleEvent.BookmarkRemoved
            } else {
                NewsArticleEvent.BookmarkAdded
            }
        }
    }

    fun consumeEvent() {
        events.value = null
    }
}
