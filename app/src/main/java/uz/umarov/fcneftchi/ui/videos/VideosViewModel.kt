package uz.umarov.fcneftchi.ui.videos

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
import uz.umarov.fcneftchi.data.model.Video
import uz.umarov.fcneftchi.domain.usecase.GetVideosUseCase
import uz.umarov.fcneftchi.domain.usecase.ObserveBookmarkedVideosUseCase
import uz.umarov.fcneftchi.domain.usecase.ToggleVideoBookmarkUseCase
import javax.inject.Inject

data class VideosUiState(
    val videos: List<Video> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val bookmarkedIds: Set<String> = emptySet(),
)

sealed interface VideosEvent {
    object BookmarkAdded : VideosEvent
    object BookmarkRemoved : VideosEvent
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class VideosViewModel @Inject constructor(
    private val getVideos: GetVideosUseCase,
    private val observeBookmarkedVideos: ObserveBookmarkedVideosUseCase,
    private val toggleVideoBookmark: ToggleVideoBookmarkUseCase,
) : ViewModel() {

    private val retryTrigger = MutableStateFlow(0)
    private val events = MutableStateFlow<VideosEvent?>(null)

    val uiState: StateFlow<VideosUiState> = retryTrigger
        .flatMapLatest {
            val videosFlow = getVideos()
                .map<List<Video>, VideosUiState> { videos ->
                    VideosUiState(videos = videos, isLoading = false)
                }
                .onStart { emit(VideosUiState(isLoading = true)) }
                .catch { e -> emit(VideosUiState(isLoading = false, error = e.message)) }
            combine(videosFlow, observeBookmarkedVideos()) { state, bookmarked ->
                state.copy(bookmarkedIds = bookmarked.map { it.id }.toSet())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), VideosUiState())

    val event: StateFlow<VideosEvent?> = events

    fun retry() {
        retryTrigger.value += 1
    }

    fun toggleBookmark(video: Video) {
        val wasBookmarked = video.id in uiState.value.bookmarkedIds
        viewModelScope.launch {
            toggleVideoBookmark(video)
            events.value = if (wasBookmarked) {
                VideosEvent.BookmarkRemoved
            } else {
                VideosEvent.BookmarkAdded
            }
        }
    }

    fun consumeEvent() {
        events.value = null
    }
}
