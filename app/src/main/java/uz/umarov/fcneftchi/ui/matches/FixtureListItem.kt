package uz.umarov.fcneftchi.ui.matches

import uz.umarov.fcneftchi.data.model.Match

sealed class FixtureListItem {
    data class HeaderItem(val monthYear: String) : FixtureListItem()
    data class MatchItem(val match: Match) : FixtureListItem()
}