package uz.umarov.fcneftchi.data.repository

import kotlinx.coroutines.flow.Flow
import uz.umarov.fcneftchi.data.model.GameDetail
import uz.umarov.fcneftchi.data.model.LeagueStanding
import uz.umarov.fcneftchi.data.model.Match
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.data.model.Player
import uz.umarov.fcneftchi.data.model.PlayerProfile
import uz.umarov.fcneftchi.data.model.Squad
import uz.umarov.fcneftchi.data.model.StatisticsData
import uz.umarov.fcneftchi.data.model.TopPlayer
import uz.umarov.fcneftchi.data.model.Video

interface NeftchiRepository {
    fun getTeam(): Flow<List<Player>>
//    fun getSquads(): Flow<List<Squad>>
    fun getClubStatistics(): Flow<StatisticsData?>
    fun getNews(): Flow<List<NewsArticle>>
    fun getNewsArticleByUrl(url: String): Flow<NewsArticle?>
    fun getPlayerProfile(playerId: Int): Flow<PlayerProfile?>
    fun getNextMatch(): Flow<Match>
    fun getLastMatch(): Flow<Match>
    fun getAllFixtures(): Flow<List<Match>>
    fun getAllResults(): Flow<List<Match>>
    fun getLeagueTable(): Flow<List<LeagueStanding>>
    fun getTopPlayers(): Flow<List<TopPlayer>>
    fun getGameDetails(gameId: Int): Flow<GameDetail?>
    fun getVideos(): Flow<List<Video>>
}
