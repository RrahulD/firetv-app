package tv.accedo.dishonstream2.data.networking.client

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tv.accedo.dishonstream2.data.networking.service.*
import tv.accedo.dishonstream2.domain.BuildConfig

class DishRetrofitClient {

    companion object {
        private const val ACCEDO_HTTP_LOG_ENABLED = true
        private const val ATX_HTTP_LOG_ENABLED = true
        private const val SUPAIR_HTTP_LOG_ENABLED = true
        private const val WEATHER_HTTP_LOG_ENABLED = true
        private const val NAGRASTAR_HTTP_LOG_ENABLED = true
        private const val REELGOOD_HTTP_LOG_ENABLED = true
    }

    fun getAccedoExternalApiService(accedoBaseUrl: String): AccedoExternalApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("$accedoBaseUrl/")
            .addConverterFactory(GsonConverterFactory.create())

        if (BuildConfig.DEBUG && ACCEDO_HTTP_LOG_ENABLED)
            retrofit.client(
                OkHttpClient.Builder().apply {
                    addInterceptor(getLoggingInterceptor())
                }.build()
            )
        return retrofit.build().create(AccedoExternalApiService::class.java)
    }

    fun getDishSmartboxApiService(atxBaseUrl: String): DishSmartboxApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("$atxBaseUrl/")
            .addConverterFactory(GsonConverterFactory.create())

        if (BuildConfig.DEBUG && ATX_HTTP_LOG_ENABLED)
            retrofit.client(
                OkHttpClient.Builder().apply {
                    addInterceptor(getLoggingInterceptor())
                }.build()
            )
        return retrofit.build().create(DishSmartboxApiService::class.java)
    }

    fun getDishSupairApiService(supairBaseUrl: String): DishSupairApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("$supairBaseUrl/")
            .addConverterFactory(GsonConverterFactory.create())

        if (BuildConfig.DEBUG && SUPAIR_HTTP_LOG_ENABLED)
            retrofit.client(
                OkHttpClient.Builder().apply {
                    addInterceptor(getLoggingInterceptor())
                }.build()
            )
        return retrofit.build().create(DishSupairApiService::class.java)
    }

    fun getDishWeatherApiService(weatherBaseUrl: String): DishWeatherApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("$weatherBaseUrl/")
            .addConverterFactory(GsonConverterFactory.create())

        if (BuildConfig.DEBUG && WEATHER_HTTP_LOG_ENABLED)
            retrofit.client(
                OkHttpClient.Builder().apply {
                    addInterceptor(getLoggingInterceptor())
                }.build()
            )
        return retrofit.build().create(DishWeatherApiService::class.java)
    }

    fun getNagrastarApiService(gameFinderBaseUrl: String): NagrastarApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("$gameFinderBaseUrl/")
            .addConverterFactory(GsonConverterFactory.create())

        if (BuildConfig.DEBUG && NAGRASTAR_HTTP_LOG_ENABLED)
            retrofit.client(
                OkHttpClient.Builder().apply {
                    addInterceptor(getLoggingInterceptor())
                }.build()
            )
        return retrofit.build().create(NagrastarApiService::class.java)
    }

    fun getReelGodApiService(reelGoodBaseUrl: String): ReelGoodApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("$reelGoodBaseUrl/")
            .addConverterFactory(GsonConverterFactory.create())

        if (BuildConfig.DEBUG && REELGOOD_HTTP_LOG_ENABLED)
            retrofit.client(
                OkHttpClient.Builder().apply {
                    addInterceptor(getLoggingInterceptor())
                }.build()
            )
        return retrofit.build().create(ReelGoodApiService::class.java)
    }

    private fun getLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
}