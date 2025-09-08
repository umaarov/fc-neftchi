package uz.umarov.fcneftchi.ui.topplayers

import uz.umarov.fcneftchi.data.model.TopPlayer
import uz.umarov.fcneftchi.ui.topplayers.adapter.TopPlayerAdapter

sealed class TopPlayerListItem {
    data class HeaderItem(val title: String) : TopPlayerListItem()
    data class PlayerItem(
        val player: TopPlayer,
        val statType: TopPlayerAdapter.StatType
    ) : TopPlayerListItem()
}