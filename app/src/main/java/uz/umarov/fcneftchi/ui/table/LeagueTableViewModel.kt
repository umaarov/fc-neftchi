package uz.umarov.fcneftchi.ui.table

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.data.model.LeagueStanding
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import javax.inject.Inject

private val SEASONS_MAP = mapOf(
    "2026" to 11,
    "2025" to 10,
    "2024" to 1,
    "2023" to 2,
    "2022" to 3
)
private const val DEFAULT_SEASON_NAME = "2026"

data class LeagueTableUiState(
    val standings: List<LeagueStanding> = emptyList(),
    val isLoading: Boolean = true,
    val selectedSeasonName: String = DEFAULT_SEASON_NAME,
    val availableSeasons: Map<String, Int> = SEASONS_MAP
)

@HiltViewModel
class LeagueTableViewModel @Inject constructor(
    private val repository: NeftchiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeagueTableUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadTableForSeason(DEFAULT_SEASON_NAME)
    }

    fun changeSeason(seasonName: String) {
        val seasonId = _uiState.value.availableSeasons[seasonName]
        if (seasonId != null) {
            _uiState.update {
                it.copy(isLoading = true, selectedSeasonName = seasonName)
            }
            loadTableForSeason(seasonName)
        }
    }

    private fun loadTableForSeason(seasonName: String) {
        val seasonId = _uiState.value.availableSeasons[seasonName] ?: return

        viewModelScope.launch {
            repository.getLeagueTable(seasonId).collect { standings ->
                _uiState.update {
                    it.copy(standings = standings, isLoading = false)
                }
            }
        }
    }
}