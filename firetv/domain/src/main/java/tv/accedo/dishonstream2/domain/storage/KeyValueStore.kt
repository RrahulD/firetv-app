package tv.accedo.dishonstream2.domain.storage

interface KeyValueStore {
    suspend fun writeBoolean(key: String, value: Boolean)
    suspend fun writeInt(key: String, value: Int)
    suspend fun writeString(key: String, value: String)
    suspend fun writeStringSet(key: String, value: LinkedHashSet<String>)

    suspend fun readBoolean(key: String, defaultValue: Boolean): Boolean
    suspend fun readInt(key: String, defaultValue: Int): Int
    suspend fun readString(key: String, defaultValue: String?): String?
    suspend fun readStringSet(key: String): Set<String>?
}