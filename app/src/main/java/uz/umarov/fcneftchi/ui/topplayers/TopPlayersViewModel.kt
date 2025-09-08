package uz.umarov.fcneftchi.ui.topplayers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import uz.umarov.fcneftchi.ui.topplayers.adapter.TopPlayerAdapter
import javax.inject.Inject

data class TopPlayersUiState(
    val items: List<TopPlayerListItem> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class TopPlayersViewModel @Inject constructor(repository: NeftchiRepository) : ViewModel() {
    val uiState: StateFlow<TopPlayersUiState> = repository.getTopPlayers()
        .map { topPlayers ->
            val combinedList = mutableListOf<TopPlayerListItem>()

            val topScorers = topPlayers.sortedByDescending { it.goals }.take(10)
            if (topScorers.isNotEmpty()) {
                combinedList.add(TopPlayerListItem.HeaderItem("Eng yaxshi to'purarlar"))
                topScorers.forEach { player ->
                    combinedList.add(TopPlayerListItem.PlayerItem(player, TopPlayerAdapter.StatType.GOALS))
                }
            }

            val topAssisters = topPlayers.sortedByDescending { it.assists }.take(10)
            if (topAssisters.isNotEmpty()) {
                combinedList.add(TopPlayerListItem.HeaderItem("Eng yaxshi assistentlar"))
                topAssisters.forEach { player ->
                    combinedList.add(TopPlayerListItem.PlayerItem(player, TopPlayerAdapter.StatType.ASSISTS))
                }
            }

            TopPlayersUiState(items = combinedList, isLoading = false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TopPlayersUiState()
        )
}