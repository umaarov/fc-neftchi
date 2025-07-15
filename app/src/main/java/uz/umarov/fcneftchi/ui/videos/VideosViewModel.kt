package uz.umarov.fcneftchi.ui.videos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

}