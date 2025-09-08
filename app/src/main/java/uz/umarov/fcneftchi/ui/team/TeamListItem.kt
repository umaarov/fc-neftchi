package uz.umarov.fcneftchi.ui.team

import uz.umarov.fcneftchi.data.model.Player

sealed class TeamListItem {
    data class HeaderItem(val title: String) : TeamListItem()
    data class PlayerItem(val player: Player) : TeamListItem()
}