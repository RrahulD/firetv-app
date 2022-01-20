package tv.accedo.dishonstream2.data.networking.service

import retrofit2.http.GET
import retrofit2.http.Query
import tv.accedo.dishonstream2.data.model.accedo.SessionResponseData
import tv.accedo.dishonstream2.data.model.accedo.SmartBoxIdMappingResponseData

interface AccedoExternalApiService {

    @GET("metadata")
    suspend fun getSmartBoxIdMappings(@Query("sessionKey") sessionKey: String): SmartBoxIdMappingResponseData

    @GET("session")
    suspend fun getSessionKey(@Query("appKey") appKey: String, @Query("uuid") uuid: String): SessionResponseData
}