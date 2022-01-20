package tv.accedo.dishonstream2.domain.usecase.settings.parentalcontrols

import tv.accedo.dishonstream2.domain.storage.KeyValueStore

class SetPermittedClassificationsUseCase (
    private val keyValueStore: KeyValueStore
) {
    suspend operator fun invoke(value: LinkedHashSet<String>) {
        return keyValueStore.writeStringSet(GetPermittedClassificationsUseCase.KEY_PERMITTED_CLASSIFICATIONS, value)
    }
}