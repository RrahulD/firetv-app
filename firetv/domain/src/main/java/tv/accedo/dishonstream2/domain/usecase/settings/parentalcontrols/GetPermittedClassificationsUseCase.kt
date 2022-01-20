package tv.accedo.dishonstream2.domain.usecase.settings.parentalcontrols

import tv.accedo.dishonstream2.domain.storage.KeyValueStore

class GetPermittedClassificationsUseCase (
    private val keyValueStore: KeyValueStore
) {
    suspend operator fun invoke(): Set<String>? {
        return keyValueStore.readStringSet(KEY_PERMITTED_CLASSIFICATIONS)
    }

    companion object {
        const val KEY_PERMITTED_CLASSIFICATIONS = "permittedClassifications"
    }
}