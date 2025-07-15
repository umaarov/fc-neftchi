package uz.umarov.fcneftchi.data.repository

import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.model.*

interface NeftchiRepository {
    fun getNextMatch(): Flow<Match>
    fun getLastMatch(): Flow<Match>
    fun getAllFixtures(): Flow<List<Match>>
    fun getAllResults(): Flow<List<Match>>
//    fun getNews(): Flow<List<NewsArticle>>
//    fun getTeam(): Flow<List<Player>>
    fun getLeagueTable(): Flow<List<LeagueStanding>>
    fun getNewsArticleById(id: String): Flow<NewsArticle?>
    fun getVideos(): Flow<List<Video>>

    fun getTeam(): Flow<List<Player>>
    fun getSquads(): Flow<List<Squad>>
    fun getClubStatistics(): Flow<StatisticsData?>
    fun getNews(): Flow<List<NewsArticle>>
}
