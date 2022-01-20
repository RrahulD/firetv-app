package tv.accedo.dishonstream2.domain.usecase.settings.legalandabout

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import tv.accedo.dishonstream2.domain.repository.CMPRepository

class GetTermsOfServiceUseCase(
    private val cmpRepository: CMPRepository
) {
    
    suspend operator fun invoke(): String = withContext(Dispatchers.IO) {
        JSONObject(cmpRepository.getMetadata(TEXT_OPTIONS_KEY)).getString(TERMS_OF_SERVICE_FIRE_TV_KEY)
    }

    companion object {
        const val TEXT_OPTIONS_KEY = "settingsFireTv"
        const val TERMS_OF_SERVICE_FIRE_TV_KEY = "termsOfService"
    }
}