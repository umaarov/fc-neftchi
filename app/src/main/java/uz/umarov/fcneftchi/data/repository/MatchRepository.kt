package uz.umarov.fcneftchi.data.repository

import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.model.GameDetail
import uz.umarov.fcneftchi.data.model.Match

interface MatchRepository {
    fun getAllFixtures(): Flow<List<Match>>
    fun getAllResults(): Flow<List<Match>>
    fun getNextMatch(): Flow<Match?>
    fun getLastMatch(): Flow<Match?>
    fun getGameDetails(gameId: Int): Flow<GameDetail?>
}
