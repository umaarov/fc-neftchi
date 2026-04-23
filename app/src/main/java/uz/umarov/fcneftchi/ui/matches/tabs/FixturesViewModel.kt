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
import uz.umarov.fcneftchi.domain.usecase.GetFixturesUseCase
import uz.umarov.fcneftchi.ui.matches.FixtureListItem
import uz.umarov.fcneftchi.util.DateFormatter
import uz.umarov.fcneftchi.util.DateUtils
import javax.inject.Inject

data class FixturesUiState(
    val items: List<FixtureListItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FixturesViewModel @Inject constructor(
    private val getFixtures: GetFixturesUseCase
) : ViewModel() {
    private val retryTrigger = MutableStateFlow(0)

    val uiState: StateFlow<FixturesUiState> = retryTrigger
        .flatMapLatest {
            getFixtures()
                .map { fixtures ->
                    val sortedMatches = fixtures
                        .mapNotNull { match ->
                            val date = DateUtils.parseDate(match.matchDate)
                            if (date != null) Pair(match, date) else null
                        }
                        .sortedBy { it.second }

                    val groupedByMonth = sortedMatches.groupBy(
                        keySelector = { DateFormatter.formatMonthHeader(it.second) },
                        valueTransform = { it.first }
                    )

                    val groupedItems = mutableListOf<FixtureListItem>()
                    for ((monthYear, matchesInMonth) in groupedByMonth) {
                        groupedItems.add(FixtureListItem.HeaderItem(monthYear.uppercase()))
                        matchesInMonth.forEach { match ->
                            groupedItems.add(FixtureListItem.MatchItem(match))
                        }
                    }

                    FixturesUiState(items = groupedItems, isLoading = false)
                }
                .onStart { emit(FixturesUiState(isLoading = true)) }
                .catch { e -> emit(FixturesUiState(isLoading = false, error = e.message)) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FixturesUiState()
        )

    fun retry() {
        retryTrigger.value += 1
    }
}
