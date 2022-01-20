package tv.accedo.dishonstream2.domain.usecase.search

import tv.accedo.dishonstream2.domain.storage.KeyValueStore

class GetRecentSearchFieldsUseCase(
    private val keyValueStore: KeyValueStore
) {
    suspend operator fun invoke(): Set<String>? {
        return keyValueStore.readStringSet(KEY_RECENT_SEARCH_KEYWORDS)
    }

    companion object {
        const val KEY_RECENT_SEARCH_KEYWORDS = "recentSearchKeywords"
    }
}