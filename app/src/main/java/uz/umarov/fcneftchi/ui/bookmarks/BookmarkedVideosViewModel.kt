package uz.umarov.fcneftchi.ui.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.data.model.Video
import uz.umarov.fcneftchi.domain.usecase.ObserveBookmarkedVideosUseCase
import uz.umarov.fcneftchi.domain.usecase.ToggleVideoBookmarkUseCase
import javax.inject.Inject

@HiltViewModel
class BookmarkedVideosViewModel @Inject constructor(
    observeBookmarkedVideos: ObserveBookmarkedVideosUseCase,
    private val toggleVideoBookmark: ToggleVideoBookmarkUseCase,
) : ViewModel() {

    val videos: StateFlow<List<Video>> = observeBookmarkedVideos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun removeBookmark(video: Video) {
        viewModelScope.launch { toggleVideoBookmark(video) }
    }
}
