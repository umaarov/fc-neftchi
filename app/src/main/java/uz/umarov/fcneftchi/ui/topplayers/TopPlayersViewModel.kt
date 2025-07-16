package uz.umarov.fcneftchi.ui.topplayers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import uz.umarov.fcneftchi.data.model.TopPlayer
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import javax.inject.Inject

data class TopPlayersUiState(
    val topScorers: List<TopPlayer> = emptyList(),
    val topAssisters: List<TopPlayer> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class TopPlayersViewModel @Inject constructor(repository: NeftchiRepository) : ViewModel() {
    val uiState: StateFlow<TopPlayersUiState> = repository.getTopPlayers()
        .map { topPlayers ->
            TopPlayersUiState(
                topScorers = topPlayers.sortedByDescending { it.goals }.take(20),
                topAssisters = topPlayers.sortedByDescending { it.assists }.take(20),
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TopPlayersUiState()
        )
}