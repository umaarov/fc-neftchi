package uz.umarov.fcneftchi.ui.stats

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
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import uz.umarov.fcneftchi.data.model.StatisticsData
import uz.umarov.fcneftchi.data.model.TopPlayer
import uz.umarov.fcneftchi.domain.usecase.GetClubStatisticsUseCase
import uz.umarov.fcneftchi.domain.usecase.GetTopPlayersUseCase
import javax.inject.Inject

data class StatisticsUiState(
    val statsData: StatisticsData? = null,
    val topScorers: List<TopPlayer> = emptyList(),
    val topAssisters: List<TopPlayer> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getClubStatistics: GetClubStatisticsUseCase,
    private val getTopPlayers: GetTopPlayersUseCase
) : ViewModel() {

    private val retryTrigger = MutableStateFlow(0)

    val uiState: StateFlow<StatisticsUiState> = retryTrigger
        .flatMapLatest {
            combine(
                getClubStatistics(),
                getTopPlayers()
            ) { clubStats, topPlayers ->
                StatisticsUiState(
                    statsData = clubStats,
                    topScorers = topPlayers.sortedByDescending { it.goals }.take(10),
                    topAssisters = topPlayers.sortedByDescending { it.assists }.take(10),
                    isLoading = false
                )
            }
                .onStart { emit(StatisticsUiState(isLoading = true)) }
                .catch { e -> emit(StatisticsUiState(isLoading = false, error = e.message)) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StatisticsUiState())

    fun retry() {
        retryTrigger.value += 1
    }
}
