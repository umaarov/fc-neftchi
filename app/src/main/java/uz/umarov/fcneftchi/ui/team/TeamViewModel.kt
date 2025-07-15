package uz.umarov.fcneftchi.ui.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.data.model.Squad
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import javax.inject.Inject

data class TeamUiState(
    val squads: List<Squad> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class TeamViewModel @Inject constructor(
    private val repository: NeftchiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeamUiState())
    val uiState: StateFlow<TeamUiState> = _uiState.asStateFlow()

    init {
        loadTeam()
    }

    private fun loadTeam() {
        viewModelScope.launch {
            repository.getSquads().collect { squads ->
                _uiState.value = TeamUiState(squads = squads, isLoading = false)
            }
        }
    }
}