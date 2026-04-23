package uz.umarov.fcneftchi.ui.table

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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import uz.umarov.fcneftchi.data.ClubConfig
import uz.umarov.fcneftchi.data.model.LeagueStanding
import uz.umarov.fcneftchi.domain.usecase.GetLeagueTableUseCase
import javax.inject.Inject

data class LeagueTableUiState(
    val standings: List<LeagueStanding> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val selectedSeasonName: String = ClubConfig.DEFAULT_SEASON_NAME,
    val availableSeasons: Map<String, Int> = ClubConfig.SEASONS_BY_NAME
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LeagueTableViewModel @Inject constructor(
    private val getLeagueTable: GetLeagueTableUseCase
) : ViewModel() {

    private val selectedSeason = MutableStateFlow(ClubConfig.DEFAULT_SEASON_NAME)
    private val retryTrigger = MutableStateFlow(0)

    val uiState: StateFlow<LeagueTableUiState> =
        combine(selectedSeason, retryTrigger) { seasonName, _ -> seasonName }
            .flatMapLatest { seasonName ->
                val seasonId = ClubConfig.SEASONS_BY_NAME[seasonName]
                if (seasonId == null) {
                    flowOf(LeagueTableUiState(selectedSeasonName = seasonName, isLoading = false))
                } else {
                    getLeagueTable(seasonId)
                        .map { standings ->
                            LeagueTableUiState(
                                standings = standings,
                                isLoading = false,
                                selectedSeasonName = seasonName
                            )
                        }
                        .onStart { emit(LeagueTableUiState(isLoading = true, selectedSeasonName = seasonName)) }
                        .catch { e ->
                            emit(
                                LeagueTableUiState(
                                    isLoading = false,
                                    error = e.message,
                                    selectedSeasonName = seasonName
                                )
                            )
                        }
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LeagueTableUiState())

    fun changeSeason(seasonName: String) {
        if (ClubConfig.SEASONS_BY_NAME.containsKey(seasonName)) {
            selectedSeason.value = seasonName
        }
    }

    fun retry() {
        retryTrigger.value += 1
    }
}
