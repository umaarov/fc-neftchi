package uz.umarov.fcneftchi.ui.matches.tabs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import uz.umarov.fcneftchi.data.model.Match
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import javax.inject.Inject

data class ResultsUiState(val results: List<Match> = emptyList(), val isLoading: Boolean = true)

@HiltViewModel
class ResultsViewModel @Inject constructor(repository: NeftchiRepository) : ViewModel() {
    val uiState: StateFlow<ResultsUiState> = repository.getAllResults()
        .map { ResultsUiState(it, false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ResultsUiState()
        )
}