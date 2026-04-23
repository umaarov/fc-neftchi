package uz.umarov.fcneftchi.ui.player

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
import uz.umarov.fcneftchi.data.model.PlayerProfile
import uz.umarov.fcneftchi.domain.usecase.GetPlayerProfileUseCase
import javax.inject.Inject

data class PlayerProfileUiState(
    val profile: PlayerProfile? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlayerProfileViewModel @Inject constructor(
    private val getPlayerProfile: GetPlayerProfileUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val playerId: Int = savedStateHandle.get<Int>("playerId")!!
    private val retryTrigger = MutableStateFlow(0)

    val uiState: StateFlow<PlayerProfileUiState> = retryTrigger
        .flatMapLatest {
            getPlayerProfile(playerId)
                .map { profile -> PlayerProfileUiState(profile = profile, isLoading = false) }
                .onStart { emit(PlayerProfileUiState(isLoading = true)) }
                .catch { e -> emit(PlayerProfileUiState(isLoading = false, error = e.message)) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlayerProfileUiState())

    fun retry() {
        retryTrigger.value += 1
    }
}
