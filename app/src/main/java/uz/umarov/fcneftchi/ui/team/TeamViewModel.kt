package uz.umarov.fcneftchi.ui.team

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
import uz.umarov.fcneftchi.data.model.Player
import uz.umarov.fcneftchi.data.model.PlayerPosition
import uz.umarov.fcneftchi.domain.usecase.GetSquadUseCase
import javax.inject.Inject

data class TeamUiState(
    val items: List<TeamListItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TeamViewModel @Inject constructor(
    private val getSquad: GetSquadUseCase
) : ViewModel() {

    private val retryTrigger = MutableStateFlow(0)

    val uiState: StateFlow<TeamUiState> = retryTrigger
        .flatMapLatest {
            getSquad()
                .map { players -> TeamUiState(items = groupByPosition(players), isLoading = false) }
                .onStart { emit(TeamUiState(isLoading = true)) }
                .catch { e -> emit(TeamUiState(isLoading = false, error = e.message)) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TeamUiState())

    fun retry() {
        retryTrigger.value += 1
    }

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
