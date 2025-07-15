package uz.umarov.fcneftchi.data.repository

import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.model.*

interface NeftchiRepository {
    fun getTeam(): Flow<List<Player>>
    fun getSquads(): Flow<List<Squad>>
    fun getClubStatistics(): Flow<StatisticsData?>
    fun getNews(): Flow<List<NewsArticle>>
    fun getNewsArticleByUrl(url: String): Flow<NewsArticle?>
    fun getPlayerProfile(playerId: Int): Flow<PlayerProfile?>
    fun getNextMatch(): Flow<Match>
    fun getLastMatch(): Flow<Match>
    fun getAllFixtures(): Flow<List<Match>>
    fun getAllResults(): Flow<List<Match>>
}
