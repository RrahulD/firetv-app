package tv.accedo.dishonstream2.data.networking.service

import retrofit2.http.GET
import retrofit2.http.Path
import tv.accedo.dishonstream2.data.model.supair.ChannelInfoData
import tv.accedo.dishonstream2.data.model.supair.ProgramInfoData

interface DishSupairApiService {
    @GET("{serviceKey}/channel_logo_info_{serviceKey}.json")
    suspend fun getChannelInfo(@Path("serviceKey") serviceKey: Long): ChannelInfoData

    @GET("{echoStarId}.json")
    suspend fun getProgramInfo(@Path("echoStarId") echoStarId: String): ProgramInfoData
}