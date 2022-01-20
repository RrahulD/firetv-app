package tv.accedo.dishonstream2.domain.usecase.settings.appsettings.tvguide

import tv.accedo.dishonstream2.domain.repository.CMPRepository
import tv.accedo.dishonstream2.domain.storage.KeyValueStore

class GetTVGuideStyleUseCase(
    private val keyValueStore: KeyValueStore,
    private val cmpRepository: CMPRepository
) {
    suspend operator fun invoke(): String {
        return keyValueStore.readString(KEY_TV_GUIDE_STYLE, null)
            ?: cmpRepository.getMetadata(KEY_TV_GUIDE_STYLE)
    }

    companion object {
        const val KEY_TV_GUIDE_STYLE = "tvGuideStyleFireTv"
    }
}