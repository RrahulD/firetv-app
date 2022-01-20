package tv.accedo.dishonstream2.domain.usecase.settings.appsettings.tvguide

import tv.accedo.dishonstream2.domain.storage.KeyValueStore

class SetTVGuideStyleUseCase(
    private val keyValueStore: KeyValueStore
) {
    suspend operator fun invoke(value: String) {
        return keyValueStore.writeString(GetTVGuideStyleUseCase.KEY_TV_GUIDE_STYLE, value)
    }
}