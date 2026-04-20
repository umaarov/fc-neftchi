package uz.umarov.fcneftchi.data.mapper

import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.data.model.ApiPlayer
import uz.umarov.fcneftchi.data.model.Player

fun ApiPlayer.toPlayer(): Player = Player(
    id = id,
    name = "${firstName ?: ""} $lastName".trim(),
    number = number ?: 0,
    position = mapPositionId(position),
    imageUrl = photo ?: R.drawable.player_placeholder_inset,
    nationality = countryTitle
)

private fun mapPositionId(positionId: Int): String = when (positionId) {
    1 -> "Darvozabon"
    2 -> "Himoyachi"
    3 -> "Yarim himoyachi"
    4 -> "Hujumchi"
    else -> "Noma'lum"
}
