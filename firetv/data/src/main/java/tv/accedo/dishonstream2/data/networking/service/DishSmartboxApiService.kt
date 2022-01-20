package tv.accedo.dishonstream2.data.networking.service

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import tv.accedo.dishonstream2.data.model.smartbox.DRMData
import tv.accedo.dishonstream2.data.model.smartbox.EPGData
import tv.accedo.dishonstream2.data.model.smartbox.SmartBoxIdentifierData

interface DishSmartboxApiService {
    @GET("{endpoint}")
    suspend fun getEPG(@Path("endpoint", encoded = true) endpoint: String): EPGData
//    @GET("https://run.mocky.io/v3/f8d09692-baa6-42eb-971c-198c14ad554a")
//    suspend fun getEPG(@Query("q") endpoint: String): EPGData

    @GET("serviceepginterface/drmtoken")
    suspend fun getDRMToken(@Query("contentID") contentID: String): DRMData

    @GET("serviceepginterface/identifier")
    suspend fun getIdentifier(): SmartBoxIdentifierData
}