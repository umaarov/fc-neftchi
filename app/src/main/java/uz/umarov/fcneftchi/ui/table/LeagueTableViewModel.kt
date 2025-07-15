package uz.umarov.fcneftchi.ui.table

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.data.model.LeagueStanding
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import javax.inject.Inject

data class LeagueTableUiState(
    val standings: List<LeagueStanding> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class LeagueTableViewModel @Inject constructor(
    private val repository: NeftchiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeagueTableUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadTable()
    }

    private fun loadTable() {
        viewModelScope.launch {
            repository.getLeagueTable().collect { standings ->
                _uiState.value = LeagueTableUiState(standings = standings, isLoading = false)
            }
        }
    }
}