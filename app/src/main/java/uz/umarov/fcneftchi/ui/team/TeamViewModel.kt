package uz.umarov.fcneftchi.ui.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import uz.umarov.fcneftchi.data.model.Player
import uz.umarov.fcneftchi.data.model.PlayerPosition
import uz.umarov.fcneftchi.domain.usecase.GetSquadUseCase
import javax.inject.Inject

data class TeamUiState(
    val items: List<TeamListItem> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class TeamViewModel @Inject constructor(
    getSquad: GetSquadUseCase
) : ViewModel() {

    val uiState: StateFlow<TeamUiState> = getSquad()
        .map { players -> TeamUiState(items = groupByPosition(players), isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TeamUiState())

    private fun groupByPosition(players: List<Player>): List<TeamListItem> {
        val positionOrder = listOf(
            PlayerPosition.GOALKEEPER,
            PlayerPosition.DEFENDER,
            PlayerPosition.MIDFIELDER,
            PlayerPosition.ATTACKER
        )
        val playersByPosition = players.groupBy { it.position }
        val sortedPositions = playersByPosition.keys.sortedWith(compareBy { position ->
            val index = positionOrder.indexOf(position)
            if (index == -1) Int.MAX_VALUE else index
        })

        val grouped = mutableListOf<TeamListItem>()
        sortedPositions.forEach { position ->
            playersByPosition[position]?.let { playerGroup ->
                grouped.add(TeamListItem.HeaderItem(position))
                playerGroup.sortedBy { it.number }.forEach { player ->
                    grouped.add(TeamListItem.PlayerItem(player))
                }
            }
        }
        return grouped
    }
}
