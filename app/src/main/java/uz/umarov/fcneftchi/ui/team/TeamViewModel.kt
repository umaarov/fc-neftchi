package uz.umarov.fcneftchi.ui.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import java.util.Locale
import javax.inject.Inject

data class TeamUiState(
    val items: List<TeamListItem> = emptyList(),
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
            _uiState.update { it.copy(isLoading = true) }
            repository.getTeam().collect { players ->
                val groupedItems = mutableListOf<TeamListItem>()

                val positionOrder = listOf("darvozabon", "himoyachi", "yarim himoyachi", "hujumchi")

                val playersByPosition =
                    players.groupBy { it.position.trim().lowercase(Locale.ROOT) }

                val sortedPositions = playersByPosition.keys.sortedWith(compareBy { position ->
                    val index = positionOrder.indexOf(position)
                    if (index == -1) Int.MAX_VALUE else index
                })

                sortedPositions.forEach { position ->
                    playersByPosition[position]?.let { playerGroup ->
                        val headerTitle =
                            position.replaceFirstChar { it.titlecase(Locale.ROOT) } + "LAR"
                        groupedItems.add(TeamListItem.HeaderItem(headerTitle.uppercase()))

                        val sortedPlayers = playerGroup.sortedBy { it.number }
                        sortedPlayers.forEach { player ->
                            groupedItems.add(TeamListItem.PlayerItem(player))
                        }
                    }
                }

                _uiState.update { it.copy(items = groupedItems, isLoading = false) }
            }
        }
    }
}