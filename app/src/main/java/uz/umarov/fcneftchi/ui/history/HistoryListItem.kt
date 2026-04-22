package uz.umarov.fcneftchi.ui.history

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

sealed class HistoryListItem {
    data class Header(@StringRes val titleRes: Int) : HistoryListItem()
    data class SubHeader(@StringRes val titleRes: Int) : HistoryListItem()
    data class Paragraph(@StringRes val textRes: Int) : HistoryListItem()
    data class Trophy(@StringRes val descriptionRes: Int) : HistoryListItem()
    data class HistoryImage(@DrawableRes val imageResId: Int) : HistoryListItem()
}
