package tv.accedo.dishonstream2.domain.usecase.settings.parentalcontrols.createpin

import tv.accedo.dishonstream2.domain.storage.KeyValueStore

class GetParentalControlPinUseCase(
private val keyValueStore: KeyValueStore
) {
    suspend operator fun invoke(): String? {
        return keyValueStore.readString(KEY_PARENTAL_CONTROL_PIN, "")
    }

    companion object {
        const val KEY_PARENTAL_CONTROL_PIN = "parentalControlPin"
    }
}