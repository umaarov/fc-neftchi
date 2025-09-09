package uz.umarov.fcneftchi.ui.history

import androidx.annotation.DrawableRes

sealed class HistoryListItem {
    data class Header(val title: String) : HistoryListItem()
    data class SubHeader(val title: String) : HistoryListItem()
    data class Paragraph(val text: String) : HistoryListItem()
    data class Trophy(val description: String) : HistoryListItem()
    data class HistoryImage(@DrawableRes val imageResId: Int) : HistoryListItem()
}