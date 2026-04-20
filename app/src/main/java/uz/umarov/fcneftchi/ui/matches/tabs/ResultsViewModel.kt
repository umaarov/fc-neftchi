package uz.umarov.fcneftchi.ui.matches.tabs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import uz.umarov.fcneftchi.domain.usecase.GetResultsUseCase
import uz.umarov.fcneftchi.ui.matches.ResultListItem
import uz.umarov.fcneftchi.util.DateFormatter
import uz.umarov.fcneftchi.util.DateUtils
import javax.inject.Inject

data class ResultsUiState(
    val items: List<ResultListItem> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ResultsViewModel @Inject constructor(
    getResults: GetResultsUseCase
) : ViewModel() {
    val uiState: StateFlow<ResultsUiState> = getResults()
        .map { results ->
            val sortedMatches = results
                .mapNotNull { match ->
                    val date = DateUtils.parseDate(match.matchDate)
                    if (date != null) Pair(match, date) else null
                }
                .sortedByDescending { it.second }

            val groupedByMonth = sortedMatches.groupBy(
                keySelector = { DateFormatter.formatMonthHeader(it.second) },
                valueTransform = { it.first }
            )

            val groupedItems = mutableListOf<ResultListItem>()
            for ((monthYear, matchesInMonth) in groupedByMonth) {
                groupedItems.add(ResultListItem.HeaderItem(monthYear.uppercase()))
                matchesInMonth.forEach { match ->
                    groupedItems.add(ResultListItem.ResultItem(match))
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
