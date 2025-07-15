package uz.umarov.fcneftchi.ui.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.data.model.PlayerProfile
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import javax.inject.Inject

data class PlayerProfileUiState(
    val profile: PlayerProfile? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class PlayerProfileViewModel @Inject constructor(
    private val repository: NeftchiRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val playerId: Int = savedStateHandle.get<Int>("playerId")!!

    private val _uiState = MutableStateFlow(PlayerProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            repository.getPlayerProfile(playerId).collect { profile ->
                _uiState.value = PlayerProfileUiState(profile = profile, isLoading = false)
            }
        }
    }
}