package tv.accedo.dishonstream2.domain.usecase.settings.parentalcontrols.createpin

import tv.accedo.dishonstream2.domain.storage.KeyValueStore

class SetParentalControlPinUseCase (
    private val keyValueStore: KeyValueStore
) {
    suspend operator fun invoke(value: String) {
        return keyValueStore.writeString(GetParentalControlPinUseCase.KEY_PARENTAL_CONTROL_PIN, value)
    }
}