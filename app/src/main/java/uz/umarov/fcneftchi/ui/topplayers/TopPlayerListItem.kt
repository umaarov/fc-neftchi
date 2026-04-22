package uz.umarov.fcneftchi.ui.topplayers

import androidx.annotation.StringRes
import uz.umarov.fcneftchi.data.model.TopPlayer
import uz.umarov.fcneftchi.ui.topplayers.adapter.TopPlayerAdapter

sealed class TopPlayerListItem {
    data class HeaderItem(@StringRes val titleRes: Int) : TopPlayerListItem()
    data class PlayerItem(
        val player: TopPlayer,
        val statType: TopPlayerAdapter.StatType
    ) : TopPlayerListItem()
}
