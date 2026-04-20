package uz.umarov.fcneftchi.ui.matches.tabs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import uz.umarov.fcneftchi.domain.usecase.GetFixturesUseCase
import uz.umarov.fcneftchi.ui.matches.FixtureListItem
import uz.umarov.fcneftchi.util.DateFormatter
import uz.umarov.fcneftchi.util.DateUtils
import javax.inject.Inject

data class FixturesUiState(
    val items: List<FixtureListItem> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class FixturesViewModel @Inject constructor(
    getFixtures: GetFixturesUseCase
) : ViewModel() {
    val uiState: StateFlow<FixturesUiState> = getFixtures()
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

            FixturesUiState(groupedItems, false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FixturesUiState()
        )
}
