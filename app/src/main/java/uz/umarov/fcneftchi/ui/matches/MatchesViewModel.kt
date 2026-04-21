package uz.umarov.fcneftchi.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import uz.umarov.fcneftchi.data.model.Match
import uz.umarov.fcneftchi.domain.usecase.GetFixturesUseCase
import uz.umarov.fcneftchi.domain.usecase.GetResultsUseCase
import javax.inject.Inject

data class MatchesUiState(
    val fixtures: List<Match> = emptyList(),
    val results: List<Match> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class MatchesViewModel @Inject constructor(
    getFixtures: GetFixturesUseCase,
    getResults: GetResultsUseCase
) : ViewModel() {

    val uiState: StateFlow<MatchesUiState> = combine(
        getFixtures(),
        getResults()
    ) { fixtures, results ->
        MatchesUiState(fixtures = fixtures, results = results, isLoading = false)
    }
        .catch { e -> emit(MatchesUiState(isLoading = false, error = e.message)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MatchesUiState())
}
