package uz.umarov.fcneftchi.ui.match_detail

import androidx.lifecycle.SavedStateHandle
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
import uz.umarov.fcneftchi.data.model.GameDetail
import uz.umarov.fcneftchi.domain.usecase.GetGameDetailsUseCase
import javax.inject.Inject

data class MatchDetailUiState(
    val gameDetail: GameDetail? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MatchDetailViewModel @Inject constructor(
    private val getGameDetails: GetGameDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val gameId: Int = savedStateHandle.get<Int>("gameId")!!
    private val retryTrigger = MutableStateFlow(0)

    val uiState: StateFlow<MatchDetailUiState> = retryTrigger
        .flatMapLatest {
            getGameDetails(gameId)
                .map { details -> MatchDetailUiState(gameDetail = details, isLoading = false) }
                .onStart { emit(MatchDetailUiState(isLoading = true)) }
                .catch { e -> emit(MatchDetailUiState(isLoading = false, error = e.message)) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MatchDetailUiState())

    fun retry() {
        retryTrigger.value += 1
    }
}
