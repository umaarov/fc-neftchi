package uz.umarov.fcneftchi.ui.videos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import uz.umarov.fcneftchi.data.model.Video
import uz.umarov.fcneftchi.domain.usecase.GetVideosUseCase
import javax.inject.Inject

data class VideosUiState(
    val videos: List<Video> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class VideosViewModel @Inject constructor(
    private val getVideos: GetVideosUseCase
) : ViewModel() {

    private val retryTrigger = MutableStateFlow(0)

    val uiState: StateFlow<VideosUiState> = retryTrigger
        .flatMapLatest {
            getVideos()
                .map { videos -> VideosUiState(videos = videos, isLoading = false) }
                .onStart { emit(VideosUiState(isLoading = true)) }
                .catch { e -> emit(VideosUiState(isLoading = false, error = e.message)) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), VideosUiState())

    fun retry() {
        retryTrigger.value += 1
    }
}
