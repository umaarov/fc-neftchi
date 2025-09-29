package uz.umarov.fcneftchi.ui.matches.tabs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import uz.umarov.fcneftchi.ui.matches.FixtureListItem
import uz.umarov.fcneftchi.util.DateUtils
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

data class FixturesUiState(
    val items: List<FixtureListItem> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class FixturesViewModel @Inject constructor(repository: NeftchiRepository) : ViewModel() {
    val uiState: StateFlow<FixturesUiState> = repository.getAllFixtures()
        .map { fixtures ->
            val sortedMatches = fixtures
                .mapNotNull { match ->
                    val date = DateUtils.parseDate(match.matchDate)
                    if (date != null) Pair(match, date) else null
                }
                .sortedBy { it.second }

            val groupedByMonth = sortedMatches.groupBy(
                keySelector = {
                    val formatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                    formatter.format(it.second)
                },
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