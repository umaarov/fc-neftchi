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
            val groupedItems = mutableListOf<FixtureListItem>()
            val groupedByMonth = fixtures.groupBy { match ->
                val formatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                val date = DateUtils.parseDate(match.matchDate)
                if (date != null) {
                    formatter.format(date)
                } else {
                    "Unknown Month"
                }
            }

            for ((monthYear, matchesInMonth) in groupedByMonth.toSortedMap()) {
                if (monthYear != "Unknown Month") {
                    groupedItems.add(FixtureListItem.HeaderItem(monthYear.uppercase()))
                    matchesInMonth.forEach { match ->
                        groupedItems.add(FixtureListItem.MatchItem(match))
                    }
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