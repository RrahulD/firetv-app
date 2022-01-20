package tv.accedo.dishonstream2.domain.usecase.settings.appsettings.playback

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tv.accedo.dishonstream2.domain.storage.KeyValueStore
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.playback.GetVideoQualityUseCase.Companion.VIDEO_QUALITY_KEY

class SetVideoQualityUseCase(
    private val keyValueStore: KeyValueStore
) {
    suspend operator fun invoke(quality: VideoQuality) = withContext(Dispatchers.IO) {
        keyValueStore.writeString(VIDEO_QUALITY_KEY, quality.value)
    }
}