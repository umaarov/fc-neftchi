package uz.umarov.fcneftchi.ui.videos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.data.model.Video
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import javax.inject.Inject

data class VideosUiState(
    val videos: List<Video> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class VideosViewModel @Inject constructor(
    private val repository: NeftchiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VideosUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadVideos()
    }

    private fun loadVideos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getVideos().collect { videos ->
                _uiState.update { it.copy(videos = videos, isLoading = false) }
            }
        }
    }
}