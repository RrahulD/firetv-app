package tv.accedo.dishonstream2.domain.usecase.settings.appsettings.playback

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tv.accedo.dishonstream2.domain.storage.KeyValueStore

enum class VideoQuality(val value: String) {
    AUTO("auto"),
    HIGH("high"),
    MEDIUM("medium"),
    LOW("low")
}

class GetVideoQualityUseCase(
    private val keyValueStore: KeyValueStore
) {
    suspend operator fun invoke(): VideoQuality = withContext(Dispatchers.IO) {
        val videoQualityString = keyValueStore.readString(VIDEO_QUALITY_KEY, VideoQuality.AUTO.value)
        VideoQuality.values().first { it.value == videoQualityString }
    }

    companion object {
        const val VIDEO_QUALITY_KEY = "videoQuality"
    }
}