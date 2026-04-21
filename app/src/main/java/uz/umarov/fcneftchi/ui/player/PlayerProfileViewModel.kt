package uz.umarov.fcneftchi.ui.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import uz.umarov.fcneftchi.data.model.PlayerProfile
import uz.umarov.fcneftchi.domain.usecase.GetPlayerProfileUseCase
import javax.inject.Inject

data class PlayerProfileUiState(
    val profile: PlayerProfile? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class PlayerProfileViewModel @Inject constructor(
    getPlayerProfile: GetPlayerProfileUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val playerId: Int = savedStateHandle.get<Int>("playerId")!!

    val uiState: StateFlow<PlayerProfileUiState> = getPlayerProfile(playerId)
        .map { profile -> PlayerProfileUiState(profile = profile, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlayerProfileUiState())
}
