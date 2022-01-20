package tv.accedo.dishonstream2.data.repository.caching

import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
interface CacheStore {
    fun <T : Any> store(key: String, data: T, validity: Duration)
    fun <T : Any> get(key: String): T?
    fun clearAllCache()
}