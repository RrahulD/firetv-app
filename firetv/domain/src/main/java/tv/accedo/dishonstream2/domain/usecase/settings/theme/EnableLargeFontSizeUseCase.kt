package tv.accedo.dishonstream2.domain.usecase.settings.theme

import tv.accedo.dishonstream2.domain.storage.KeyValueStore

class EnableLargeFontSizeUseCase(
    private val keyValueStore: KeyValueStore
) {
    suspend operator fun invoke(shouldEnable: Boolean) {
        keyValueStore.writeBoolean(IsLargeFontSizeEnabledUseCase.KEY_IS_LARGE_FONT_ENABLED, shouldEnable)
    }
}