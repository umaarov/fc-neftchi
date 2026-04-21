package uz.umarov.fcneftchi.ui.videos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import uz.umarov.fcneftchi.data.model.Video
import uz.umarov.fcneftchi.domain.usecase.GetVideosUseCase
import javax.inject.Inject

data class VideosUiState(
    val videos: List<Video> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class VideosViewModel @Inject constructor(
    getVideos: GetVideosUseCase
) : ViewModel() {

    val uiState: StateFlow<VideosUiState> = getVideos()
        .map { videos -> VideosUiState(videos = videos, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), VideosUiState())
}
