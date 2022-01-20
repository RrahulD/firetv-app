package tv.accedo.dishonstream2.data.repository

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONArray
import timber.log.Timber
import tv.accedo.dishonstream2.data.exception.NoSmartBoxIdFoundException
import tv.accedo.dishonstream2.data.networking.client.DishRetrofitClient
import tv.accedo.dishonstream2.domain.model.control.Endpoints
import tv.accedo.dishonstream2.domain.model.home.template.EntryEntity
import tv.accedo.dishonstream2.domain.model.home.template.EntryType
import tv.accedo.dishonstream2.domain.repository.CMPRepository
import tv.accedo.one.sdk.definition.AccedoOne
import tv.accedo.one.sdk.implementation.AccedoOneImpl

class A1ControlRepositoryV2Impl(
    private val context: Context,
    private val dishRetrofitClient: DishRetrofitClient,
    private val deviceId: String
) : CMPRepository {
    private var allMetaData: Map<String, String>? = null
    private var allAsset: Map<String, String>? = null
    private var allEntries: Map<String, EntryEntity>? = null
    private lateinit var a1: AccedoOne

    companion object {
        private const val ENDPOINTS = "endpoints"
        private const val DEFAULT_PROPERTY_NAME = "DEFAULT_PROPERTY"
    }

    override suspend fun initialize(smartBoxId: String) {
        // when we get the Smart Box identifier we create the AccedoOne control object.
        // this method has many hardcoded data which is unavoidable. this will be cleaned up once we
        // migrate to Dish CMP
        val service = dishRetrofitClient.getAccedoExternalApiService("https://api.one.accedo.tv")
        val sessionKey = service.getSessionKey("612675df1de1c4001668a8d4", "612675b91de1c4001668a8d3").sessionKey
        val smartBoxMappings = service.getSmartBoxIdMappings(sessionKey).toSmartBoxMappingData().mappings
        val appKey = (smartBoxMappings[smartBoxId] ?: smartBoxMappings[DEFAULT_PROPERTY_NAME])?.appKey
            ?: throw NoSmartBoxIdFoundException()
        a1 = AccedoOneImpl(appKey, deviceId)
    }

    override suspend fun getMetadata(key: String): String {
        return allMetaData?.get(key) ?: getAllMetadata().getValue(key)
    }

    override suspend fun getAsset(key: String): String {
        return allAsset?.get(key) ?: getAllAssets().getValue(key)
    }

    override suspend fun getEndpoints(): Endpoints {
        return getEndpointsFromMetadata()
    }

    override suspend fun getEntryById(id: String): EntryEntity {
        return allEntries?.get(id) ?: getAllEntries().getValue(id)
    }

    override suspend fun getAllEntries(): Map<String, EntryEntity> = suspendCancellableCoroutine { continuation ->
        this.allEntries?.let {
            continuation.resumeWith(Result.success(it))
        } ?: run {
            a1.publish().async().getAllEntries(context, { allEntriesArray ->
                val entriesEntityMap = mapEntryJsonArrayToEntryEntityMap(allEntriesArray)
                this.allEntries = entriesEntityMap
                continuation.resumeWith(Result.success(entriesEntityMap))
            }, {
                continuation.resumeWith(Result.failure(it))
            }).also { cancellable ->
                continuation.invokeOnCancellation { cancellable.cancel() }
            }
        }
    }

    private fun mapEntryJsonArrayToEntryEntityMap(entries: JSONArray): Map<String, EntryEntity> {
        val entriesEntityMap = hashMapOf<String, EntryEntity>()
        for (i in 0 until entries.length()) {
            val jsonObj = entries.getJSONObject(i)
            val metaData = jsonObj.getJSONObject("_meta")
            val entryType = metaData.getString("typeAlias")
            val id = metaData.getString("id")

            EntryType.values().find { it.type == entryType }?.let { entryTypeEnum ->
                entriesEntityMap[id] = (EntryEntity(entryTypeEnum, id, jsonObj))
            }
        }
        return entriesEntityMap
    }

    override suspend fun getAllMetadata(): Map<String, String> = suspendCancellableCoroutine { continuation ->
        this.allMetaData?.let {
            continuation.resumeWith(Result.success(it))
        } ?: run {
            a1.control().async().getAllMetadata(context, {
                this.allMetaData = it
                Timber.d("Metadata => $allMetaData")
                continuation.resumeWith(Result.success(it))
            }, {
                continuation.resumeWith(Result.failure(it))
            }).also { cancellable ->
                continuation.invokeOnCancellation { cancellable.cancel() }
            }
        }
    }

    override suspend fun getAllAssets(): Map<String, String> = suspendCancellableCoroutine { continuation ->
        a1.control().async().getAllAssets(context, {
            continuation.resumeWith(Result.success(it))
            this.allAsset = it
        }, {
            continuation.resumeWith(Result.failure(it))
        }).also { cancellable ->
            continuation.invokeOnCancellation { cancellable.cancel() }
        }
    }

    private suspend fun getEndpointsFromMetadata(): Endpoints {
        val endPointsString = getMetadata(ENDPOINTS)
        return Gson().fromJson(endPointsString, Endpoints::class.java)
    }
}