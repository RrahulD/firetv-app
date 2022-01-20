package tv.accedo.dishonstream2.domain.usecase.settings.appsettings.faq

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import tv.accedo.dishonstream2.domain.repository.CMPRepository

class GetFAQUseCase(
    private val cmpRepository: CMPRepository
) {
    suspend operator fun invoke(): String = withContext(Dispatchers.IO) {
        JSONObject(cmpRepository.getMetadata(KEY_SETTINGS)).getString(KEY_FAQ)
    }

    companion object {
        private const val KEY_SETTINGS = "settingsFireTv"
        private const val KEY_FAQ = "faq"
    }
}