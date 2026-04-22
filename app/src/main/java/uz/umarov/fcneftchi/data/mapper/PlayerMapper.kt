package uz.umarov.fcneftchi.data.mapper

import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.data.model.ApiPlayer
import uz.umarov.fcneftchi.data.model.Player
import uz.umarov.fcneftchi.data.model.PlayerPosition

fun ApiPlayer.toPlayer(): Player = Player(
    id = id,
    name = "${firstName ?: ""} $lastName".trim(),
    number = number ?: 0,
    position = PlayerPosition.fromId(position),
    imageUrl = photo ?: R.drawable.player_placeholder_inset,
    nationality = countryTitle
)
