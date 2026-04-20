package uz.umarov.fcneftchi.data.repository

import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.model.Player
import uz.umarov.fcneftchi.data.model.PlayerProfile

interface PlayerRepository {
    fun getSquad(): Flow<List<Player>>
    fun getPlayerProfile(playerId: Int): Flow<PlayerProfile?>
}
