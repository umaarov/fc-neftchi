package uz.umarov.fcneftchi.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import uz.umarov.fcneftchi.data.model.ClubDetailResponse
import uz.umarov.fcneftchi.data.model.GameApiResponse
import uz.umarov.fcneftchi.data.model.GameCalendarResponse
import uz.umarov.fcneftchi.data.model.GameDetailResponse
import uz.umarov.fcneftchi.data.model.LeagueTableResponse
import uz.umarov.fcneftchi.data.model.NewsApiResponse
import uz.umarov.fcneftchi.data.model.NewsDetailResponse
import uz.umarov.fcneftchi.data.model.PlayerApiResponse
import uz.umarov.fcneftchi.data.model.PlayerDetailResponse
import uz.umarov.fcneftchi.data.model.PlayerStatisticResponse
import uz.umarov.fcneftchi.data.model.StatisticsResponse
import uz.umarov.fcneftchi.data.model.TopPlayersResponse

interface PflApiService {
    @GET("v1/web/club/{id}")
    suspend fun getClubDetails(@Path("id") id: Int): ClubDetailResponse

    @GET("v1/web/club/{id}/players")
    suspend fun getClubPlayers(
        @Path("id") clubId: Int,
    ): PlayerApiResponse

    @GET("v1/web/club/{id}/statistics")
    suspend fun getClubStatistics(@Path("id") id: Int): StatisticsResponse

    @GET("v1/web/news")
    suspend fun getNews(@Query("clubId") clubId: Int): NewsApiResponse

    @GET("v1/web/news/{url}")
    suspend fun getNewsDetail(@Path("url") url: String): NewsDetailResponse

    @GET("v1/web/player/{id}")
    suspend fun getPlayerDetails(@Path("id") playerId: Int): PlayerDetailResponse

    @GET("v1/web/player/{id}/statistic")
    suspend fun getPlayerStatistics(@Path("id") playerId: Int): PlayerStatisticResponse

    @GET("v1/web/game")
    suspend fun getGames(@Query("clubId") clubId: Int): GameApiResponse

//    @GET("v1/web/game/table")
//    suspend fun getLeagueTable(): LeagueTableResponse

    @GET("v1/web/game/table")
    suspend fun getLeagueTable(@Query("seasonId") seasonId: Int): LeagueTableResponse

    @GET("v1/web/game/table/top-players")
    suspend fun getTopPlayers(): TopPlayersResponse

    @GET("v1/web/game/{id}")
    suspend fun getGameDetails(@Path("id") gameId: Int): GameDetailResponse

    @GET("v1/web/game/calendar")
    suspend fun getGameCalendar(
        @Query("tournamentId") tournamentId: Int,
        @Query("seasonId") seasonId: Int,
        @Query("clubId") clubId: Int
    ): GameCalendarResponse
}