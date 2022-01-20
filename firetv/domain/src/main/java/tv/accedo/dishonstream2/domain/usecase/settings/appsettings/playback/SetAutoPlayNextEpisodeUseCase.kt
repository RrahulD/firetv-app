package tv.accedo.dishonstream2.domain.usecase.settings.appsettings.playback

import tv.accedo.dishonstream2.domain.storage.KeyValueStore

class SetAutoPlayNextEpisodeUseCase(
    private val keyValueStore: KeyValueStore
) {
    suspend operator fun invoke(enable: Boolean) {
        return keyValueStore
            .writeBoolean(IsAutoPlayNextEpisodeEnabledUseCase.IS_AUTO_PLAY_NEXT_EPISODE_KEY, enable)
    }
}