package uz.umarov.fcneftchi.ui.team

import uz.umarov.fcneftchi.data.model.Player
import uz.umarov.fcneftchi.data.model.PlayerPosition

sealed class TeamListItem {
    data class HeaderItem(val position: PlayerPosition) : TeamListItem()
    data class PlayerItem(val player: Player) : TeamListItem()
}
