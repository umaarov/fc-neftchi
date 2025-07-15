package uz.umarov.fcneftchi.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.data.model.StatisticsData
import uz.umarov.fcneftchi.data.model.TopPlayer
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import javax.inject.Inject

data class StatisticsUiState(
    val statsData: StatisticsData? = null,
    val topScorers: List<TopPlayer> = emptyList(),
    val topAssisters: List<TopPlayer> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: NeftchiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            combine(
                repository.getClubStatistics(),
                repository.getTopPlayers()
            ) { clubStats, topPlayers ->
                val topScorers = topPlayers.sortedByDescending { it.goals }.take(10)
                val topAssisters = topPlayers.sortedByDescending { it.assists }.take(10)

                StatisticsUiState(
                    statsData = clubStats,
                    topScorers = topScorers,
                    topAssisters = topAssisters,
                    isLoading = false
                )
            }.catch {
                _uiState.update { it.copy(isLoading = false) }
            }.collect { combinedState ->
                _uiState.value = combinedState
            }
        }
    }
}