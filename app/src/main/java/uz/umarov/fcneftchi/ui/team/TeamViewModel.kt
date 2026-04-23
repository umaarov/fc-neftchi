package uz.umarov.fcneftchi.ui.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
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
    val searchQuery: String = "",
    val isFiltered: Boolean = false,
)

private data class SquadLoad(
    val players: List<Player> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TeamViewModel @Inject constructor(
    private val getSquad: GetSquadUseCase
) : ViewModel() {

    private val retryTrigger = MutableStateFlow(0)
    private val searchQuery = MutableStateFlow("")

    private val squadLoad: StateFlow<SquadLoad> = retryTrigger
        .flatMapLatest {
            getSquad()
                .map { players -> SquadLoad(players = players, isLoading = false) }
                .onStart { emit(SquadLoad(isLoading = true)) }
                .catch { e -> emit(SquadLoad(isLoading = false, error = e.message)) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SquadLoad())

    val uiState: StateFlow<TeamUiState> =
        combine(squadLoad, searchQuery) { load, query ->
            val trimmed = query.trim()
            val filtered = if (trimmed.isBlank()) load.players else load.players.filter { it.matches(trimmed) }
            TeamUiState(
                items = groupByPosition(filtered),
                isLoading = load.isLoading,
                error = load.error,
                searchQuery = query,
                isFiltered = trimmed.isNotBlank(),
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TeamUiState())

    fun retry() {
        retryTrigger.value += 1
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    private fun Player.matches(query: String): Boolean {
        return name.contains(query, ignoreCase = true) ||
            number.toString() == query.trim()
    }

    private fun groupByPosition(players: List<Player>): List<TeamListItem> {
        if (players.isEmpty()) return emptyList()
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
