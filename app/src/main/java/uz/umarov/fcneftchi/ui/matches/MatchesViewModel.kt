package uz.umarov.fcneftchi.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.data.model.Match
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import javax.inject.Inject

data class MatchesUiState(
    val fixtures: List<Match> = emptyList(),
    val results: List<Match> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class MatchesViewModel @Inject constructor(
    private val repository: NeftchiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MatchesUiState())
    val uiState: StateFlow<MatchesUiState> = _uiState.asStateFlow()
}