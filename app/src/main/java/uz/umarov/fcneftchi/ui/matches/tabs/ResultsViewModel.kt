package uz.umarov.fcneftchi.ui.matches.tabs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import uz.umarov.fcneftchi.ui.matches.ResultListItem
import uz.umarov.fcneftchi.util.DateUtils
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

data class ResultsUiState(
    val items: List<ResultListItem> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ResultsViewModel @Inject constructor(repository: NeftchiRepository) : ViewModel() {
    val uiState: StateFlow<ResultsUiState> = repository.getAllResults()
        .map { results ->
            val groupedItems = mutableListOf<ResultListItem>()
            val groupedByMonth = results.groupBy { match ->
                val formatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                val date = DateUtils.parseDate(match.matchDate)
                if (date != null) formatter.format(date) else "Unknown Month"
            }

            for ((monthYear, matchesInMonth) in groupedByMonth.toSortedMap()) {
                if (monthYear != "Unknown Month") {
                    groupedItems.add(ResultListItem.HeaderItem(monthYear.uppercase()))
                    matchesInMonth.forEach { match ->
                        groupedItems.add(ResultListItem.ResultItem(match))
                    }
                }
            }
            ResultsUiState(groupedItems, false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ResultsUiState()
        )
}