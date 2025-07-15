package uz.umarov.fcneftchi.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import uz.umarov.fcneftchi.data.model.PlayerApiResponse

interface PflApiService {

    companion object {
        const val BASE_URL = "https://api.pfl.uz/"
    }

    @GET("v1/web/club/{id}/players")
    suspend fun getClubPlayers(@Path("id") id: Int): PlayerApiResponse

}