package uz.umarov.fcneftchi.ui.matches

import uz.umarov.fcneftchi.data.model.Match

sealed class ResultListItem {
    data class HeaderItem(val monthYear: String) : ResultListItem()
    data class ResultItem(val match: Match) : ResultListItem()
}