package uz.umarov.fcneftchi.ui.match_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.data.model.GameDetail
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import javax.inject.Inject

data class MatchDetailUiState(
    val gameDetail: GameDetail? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class MatchDetailViewModel @Inject constructor(
    private val repository: NeftchiRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val gameId: Int = savedStateHandle.get<Int>("gameId")!!

    private val _uiState = MutableStateFlow(MatchDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadGameDetails()
    }

    private fun loadGameDetails() {
        viewModelScope.launch {
            repository.getGameDetails(gameId).collect { details ->
                _uiState.value = MatchDetailUiState(gameDetail = details, isLoading = false)
            }
        }
    }
}