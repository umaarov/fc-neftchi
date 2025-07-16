package uz.umarov.fcneftchi.ui.matches.tabs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import uz.umarov.fcneftchi.data.model.Match
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import javax.inject.Inject

data class FixturesUiState(val fixtures: List<Match> = emptyList(), val isLoading: Boolean = true)

@HiltViewModel
class FixturesViewModel @Inject constructor(repository: NeftchiRepository) : ViewModel() {
    val uiState: StateFlow<FixturesUiState> = repository.getAllFixtures()
        .map { FixturesUiState(it, false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FixturesUiState()
        )
}