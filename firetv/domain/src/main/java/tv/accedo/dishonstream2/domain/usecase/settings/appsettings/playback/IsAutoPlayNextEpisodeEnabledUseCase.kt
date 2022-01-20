package tv.accedo.dishonstream2.domain.usecase.settings.appsettings.playback

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tv.accedo.dishonstream2.domain.storage.KeyValueStore

class IsAutoPlayNextEpisodeEnabledUseCase(
    private val keyValueStore: KeyValueStore
) {

    suspend operator fun invoke(): Boolean = withContext(Dispatchers.IO) {
        keyValueStore.readBoolean(IS_AUTO_PLAY_NEXT_EPISODE_KEY, false)
    }

    companion object {
        const val IS_AUTO_PLAY_NEXT_EPISODE_KEY = "autoPlayNextEpisode"
    }
}