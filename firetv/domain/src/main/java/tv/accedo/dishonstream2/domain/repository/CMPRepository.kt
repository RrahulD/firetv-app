package tv.accedo.dishonstream2.domain.repository

import tv.accedo.dishonstream2.domain.model.control.Endpoints
import tv.accedo.dishonstream2.domain.model.home.template.EntryEntity

/**
 * Interface for the Accedo One Control connection
 *
 */

interface CMPRepository {

    suspend fun initialize(smartBoxId: String)

    suspend fun getMetadata(key: String): String

    suspend fun getAllMetadata(): Map<String, String>

    suspend fun getAsset(key: String): String

    suspend fun getAllAssets(): Map<String, String>

    suspend fun getEndpoints(): Endpoints

    suspend fun getEntryById(id: String): EntryEntity

    suspend fun getAllEntries(): Map<String, EntryEntity>
}