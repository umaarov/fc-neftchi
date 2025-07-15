package uz.umarov.fcneftchi.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.data.model.StatisticsData
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import javax.inject.Inject

data class StatisticsUiState(
    val statsData: StatisticsData? = null,
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
            repository.getClubStatistics().collect { stats ->
                _uiState.value = StatisticsUiState(statsData = stats, isLoading = false)
            }
        }
    }
}