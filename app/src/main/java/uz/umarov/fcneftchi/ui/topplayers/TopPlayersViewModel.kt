package uz.umarov.fcneftchi.ui.topplayers

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
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.domain.usecase.GetTopPlayersUseCase
import uz.umarov.fcneftchi.ui.topplayers.adapter.TopPlayerAdapter
import javax.inject.Inject

data class TopPlayersUiState(
    val items: List<TopPlayerListItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TopPlayersViewModel @Inject constructor(
    private val getTopPlayers: GetTopPlayersUseCase
) : ViewModel() {

    private val retryTrigger = MutableStateFlow(0)

    val uiState: StateFlow<TopPlayersUiState> = retryTrigger
        .flatMapLatest {
            getTopPlayers()
                .map { topPlayers ->
                    val combinedList = mutableListOf<TopPlayerListItem>()

                    val topScorers = topPlayers.sortedByDescending { it.goals }.take(10)
                    if (topScorers.isNotEmpty()) {
                        combinedList.add(TopPlayerListItem.HeaderItem(R.string.top_scorers))
                        topScorers.forEach { player ->
                            combinedList.add(
                                TopPlayerListItem.PlayerItem(
                                    player,
                                    TopPlayerAdapter.StatType.GOALS
                                )
                            )
                        }
                    }

                    val topAssisters = topPlayers.sortedByDescending { it.assists }.take(10)
                    if (topAssisters.isNotEmpty()) {
                        combinedList.add(TopPlayerListItem.HeaderItem(R.string.top_assisters))
                        topAssisters.forEach { player ->
                            combinedList.add(
                                TopPlayerListItem.PlayerItem(
                                    player,
                                    TopPlayerAdapter.StatType.ASSISTS
                                )
                            )
                        }
                    }

                    TopPlayersUiState(items = combinedList, isLoading = false)
                }
                .onStart { emit(TopPlayersUiState(isLoading = true)) }
                .catch { e -> emit(TopPlayersUiState(isLoading = false, error = e.message)) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TopPlayersUiState()
        )

    fun retry() {
        retryTrigger.value += 1
    }
}
