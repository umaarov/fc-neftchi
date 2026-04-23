package uz.umarov.fcneftchi.ui.matches.tabs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import uz.umarov.fcneftchi.domain.usecase.GetResultsUseCase
import uz.umarov.fcneftchi.ui.matches.ResultListItem
import uz.umarov.fcneftchi.util.DateFormatter
import uz.umarov.fcneftchi.util.DateUtils
import javax.inject.Inject

data class ResultsUiState(
    val items: List<ResultListItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ResultsViewModel @Inject constructor(
    private val getResults: GetResultsUseCase
) : ViewModel() {
    private val retryTrigger = MutableStateFlow(0)

    val uiState: StateFlow<ResultsUiState> = retryTrigger
        .flatMapLatest {
            getResults()
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

                    ResultsUiState(items = groupedItems, isLoading = false)
                }
                .onStart { emit(ResultsUiState(isLoading = true)) }
                .catch { e -> emit(ResultsUiState(isLoading = false, error = e.message)) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ResultsUiState()
        )

    fun retry() {
        retryTrigger.value += 1
    }
}
