package tv.accedo.dishonstream2.domain.usecase.settings.appsettings

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import tv.accedo.dishonstream2.domain.repository.CMPRepository

class GetAppSettingsOptionsUseCase(
    private val cmpRepository: CMPRepository
) {
    suspend operator fun invoke(): List<String> = withContext(Dispatchers.IO) {
        val appSettingsOptions = JSONObject(cmpRepository.getMetadata(SETTINGS_FIRE_TV_KEY))
            .getJSONObject(APP_SETTINGS_KEY)
            .getJSONObject(SETTINGS_KEY)

        val options = mutableListOf<String>()

        if (appSettingsOptions.getJSONObject(PLAYBACK_KEY).getBoolean(VISIBILITY_KEY))
            options.add(PLAYBACK_KEY)

        if (appSettingsOptions.getJSONObject(TV_GUIDE_KEY).getBoolean(VISIBILITY_KEY))
            options.add(TV_GUIDE_KEY)

        if (appSettingsOptions.getJSONObject(FORMAT_OPTIONS_KEY).getBoolean(VISIBILITY_KEY))
            options.add(FORMAT_OPTIONS_KEY)

        options
    }

    companion object {
        const val SETTINGS_FIRE_TV_KEY = "settingsFireTv"
        const val APP_SETTINGS_KEY = "appSettings"
        const val SETTINGS_KEY = "settings"
        const val PLAYBACK_KEY = "playback"
        const val TV_GUIDE_KEY = "tvGuide"
        const val FORMAT_OPTIONS_KEY = "formatOptions"
        const val VISIBILITY_KEY = "visible"
    }
}