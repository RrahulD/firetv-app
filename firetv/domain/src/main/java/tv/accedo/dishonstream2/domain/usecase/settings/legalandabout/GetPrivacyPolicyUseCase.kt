package tv.accedo.dishonstream2.domain.usecase.settings.legalandabout

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import tv.accedo.dishonstream2.domain.repository.CMPRepository

class GetPrivacyPolicyUseCase(
    private val cmpRepository: CMPRepository
) {
    suspend operator fun invoke(): String = withContext(Dispatchers.IO) {
        JSONObject(cmpRepository.getMetadata(TEXT_OPTIONS_KEY)).getString(PRIVACY_POLICY_FIRE_TV_KEY)
    }

    companion object {
        const val TEXT_OPTIONS_KEY = "settingsFireTv"
        const val PRIVACY_POLICY_FIRE_TV_KEY = "privacyPolicy"
    }
}