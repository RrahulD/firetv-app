package tv.accedo.dishonstream2.domain.usecase.settings

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import tv.accedo.dishonstream2.domain.repository.CMPRepository

class GetSettingsOptionsUseCase(
    private val cmpRepository: CMPRepository
) {

    suspend operator fun invoke(): List<String> = withContext(Dispatchers.IO) {
        val settings = JSONObject(cmpRepository.getMetadata(SETTINGS_FIRE_TV_KEY))
        val options = mutableListOf<String>()

        if (settings.getJSONObject(RECORDINGS_KEY).getBoolean(VISIBILITY_KEY))
            options.add(RECORDINGS_KEY)

        if (settings.getJSONObject(ACCOUNT_KEY).getBoolean(VISIBILITY_KEY))
            options.add(ACCOUNT_KEY)

        // For now enabling this option directly without config check for Qa testing. The below check must be enabled for production
        //if (settings.getJSONObject(PARENTAL_CONTROLS_KEY).getBoolean(VISIBILITY_KEY))
        options.add(PARENTAL_CONTROLS_KEY)

        if (settings.getJSONObject(APP_SETTINGS_KEY).getBoolean(VISIBILITY_KEY))
            options.add(APP_SETTINGS_KEY)

        if (settings.getJSONObject(FAQS_KEY).getBoolean(VISIBILITY_KEY))
            options.add(FAQS_KEY)

        if (settings.getJSONObject(LEGAL_AND_ABOUT_KEY).getBoolean(VISIBILITY_KEY))
            options.add(LEGAL_AND_ABOUT_KEY)

        if (settings.getJSONObject(SIGN_OUT_KEY).getBoolean(VISIBILITY_KEY))
            options.add(SIGN_OUT_KEY)

        options
    }

    companion object {
        const val SETTINGS_FIRE_TV_KEY = "settingsFireTv"
        const val RECORDINGS_KEY = "recordings"
        const val ACCOUNT_KEY = "account"
        const val PARENTAL_CONTROLS_KEY = "parentalControls"
        const val APP_SETTINGS_KEY = "appSettings"
        const val FAQS_KEY = "faqs"
        const val LEGAL_AND_ABOUT_KEY = "legalAndAbout"
        const val SIGN_OUT_KEY = "signOut"
        const val VISIBILITY_KEY = "visible"
    }
}