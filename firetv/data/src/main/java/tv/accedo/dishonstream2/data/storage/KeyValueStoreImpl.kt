package tv.accedo.dishonstream2.data.storage

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import tv.accedo.dishonstream2.domain.storage.KeyValueStore

class KeyValueStoreImpl(
    private val application: Application
) : KeyValueStore {
    private val Context.prefDataStore: DataStore<Preferences> by preferencesDataStore("${application.packageName}_preference")

    private val preferences: DataStore<Preferences> by lazy {
        application.applicationContext.prefDataStore
    }

    override suspend fun writeBoolean(key: String, value: Boolean) {
        preferences.edit { preference ->
            preference[booleanPreferencesKey(key)] = value
        }
    }

    override suspend fun writeInt(key: String, value: Int) {
        preferences.edit { preference ->
            preference[intPreferencesKey(key)] = value
        }
    }

    override suspend fun writeString(key: String, value: String) {
        preferences.edit { preference ->
            preference[stringPreferencesKey(key)] = value
        }
    }

    override suspend fun writeStringSet(key: String, value: LinkedHashSet<String>) {
        preferences.edit { preference ->
            preference[stringSetPreferencesKey(key)] = value
        }
    }

    override suspend fun readBoolean(key: String, defaultValue: Boolean): Boolean {
        return preferences.data.firstOrNull()?.get(booleanPreferencesKey(key)) ?: defaultValue
    }

    override suspend fun readInt(key: String, defaultValue: Int): Int {
        return preferences.data.firstOrNull()?.get(intPreferencesKey(key)) ?: defaultValue
    }

    override suspend fun readString(key: String, defaultValue: String?): String? {
        return preferences.data.firstOrNull()?.get(stringPreferencesKey(key)) ?: defaultValue
    }

    override suspend fun readStringSet(key: String): Set<String>? {
        return preferences.data.firstOrNull()?.get(stringSetPreferencesKey(key))
    }
}