package uz.umarov.fcneftchi.ui.match_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import uz.umarov.fcneftchi.data.model.GameDetail
import uz.umarov.fcneftchi.domain.usecase.GetGameDetailsUseCase
import javax.inject.Inject

data class MatchDetailUiState(
    val gameDetail: GameDetail? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class MatchDetailViewModel @Inject constructor(
    getGameDetails: GetGameDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val gameId: Int = savedStateHandle.get<Int>("gameId")!!

    val uiState: StateFlow<MatchDetailUiState> = getGameDetails(gameId)
        .map { details -> MatchDetailUiState(gameDetail = details, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MatchDetailUiState())
}
