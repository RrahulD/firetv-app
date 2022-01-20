package tv.accedo.dishonstream2.domain.usecase.search

import tv.accedo.dishonstream2.domain.storage.KeyValueStore

class SetRecentSearchFieldsUseCase(
    private val keyValueStore: KeyValueStore
) {
    suspend operator fun invoke(value: LinkedHashSet<String>) {
        return keyValueStore.writeStringSet(GetRecentSearchFieldsUseCase.KEY_RECENT_SEARCH_KEYWORDS, value)
    }
}