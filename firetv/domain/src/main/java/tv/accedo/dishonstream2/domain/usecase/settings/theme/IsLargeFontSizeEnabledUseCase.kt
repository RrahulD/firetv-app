package tv.accedo.dishonstream2.domain.usecase.settings.theme

import tv.accedo.dishonstream2.domain.storage.KeyValueStore

class IsLargeFontSizeEnabledUseCase(
    private val keyValueStore: KeyValueStore
) {
    suspend operator fun invoke(): Boolean {
        return keyValueStore.readBoolean(KEY_IS_LARGE_FONT_ENABLED, false)
    }

    companion object {
        const val KEY_IS_LARGE_FONT_ENABLED = "isEnabledLargeFontSize"
    }
}