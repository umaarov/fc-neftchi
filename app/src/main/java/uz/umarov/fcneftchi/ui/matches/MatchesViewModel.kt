package uz.umarov.fcneftchi.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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
    private val getFixtures: GetFixturesUseCase,
    private val getResults: GetResultsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MatchesUiState())
    val uiState: StateFlow<MatchesUiState> = _uiState.asStateFlow()

    init {
        loadMatches()
    }

    private fun loadMatches() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            combine(
                getFixtures(),
                getResults()
            ) { fixtures, results ->
                MatchesUiState(fixtures = fixtures, results = results, isLoading = false)
            }.catch { e ->
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }.collect { combinedState ->
                _uiState.value = combinedState
            }
        }
    }
}
