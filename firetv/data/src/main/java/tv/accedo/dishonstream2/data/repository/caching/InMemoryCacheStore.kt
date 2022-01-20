package tv.accedo.dishonstream2.data.repository.caching

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class InMemoryCacheStore : CacheStore {

    private data class InMemoryCache(
        val cacheData: Any,
        val expiryTime: Instant
    ) {
        fun isExpired(): Boolean = Clock.System.now() >= expiryTime
    }

    private val cacheMap = ConcurrentHashMap<String, InMemoryCache>()

    override fun <T : Any> store(key: String, data: T, validity: Duration) {
        cacheMap[key] = InMemoryCache(
            cacheData = data,
            expiryTime = Clock.System.now() + validity
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(key: String): T? {
        val cache = cacheMap[key]

        return if (cache != null) {
            if (!cache.isExpired()) {
                try {
                    cache.cacheData as T
                } catch (ex: ClassCastException) {
                    null
                }
            } else {
                // clear the cache for this key from memory as its already expired
                cacheMap.remove(key)
                null
            }
        } else null
    }

    override fun clearAllCache() {
        cacheMap.clear()
    }
}