package uz.umarov.fcneftchi.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import uz.umarov.fcneftchi.data.model.ClubDetailResponse
import uz.umarov.fcneftchi.data.model.NewsApiResponse
import uz.umarov.fcneftchi.data.model.NewsDetailResponse
import uz.umarov.fcneftchi.data.model.PlayerApiResponse
import uz.umarov.fcneftchi.data.model.PlayerDetailResponse
import uz.umarov.fcneftchi.data.model.PlayerStatisticResponse
import uz.umarov.fcneftchi.data.model.StatisticsResponse

interface PflApiService {

    companion object {
        const val BASE_URL = "https://api.pfl.uz/"
    }

    @GET("v1/web/club/{id}")
    suspend fun getClubDetails(@Path("id") id: Int): ClubDetailResponse

    @GET("v1/web/club/{id}/players")
    suspend fun getClubPlayers(
        @Path("id") clubId: Int,
        @Query("teamId") teamId: Int
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
}