package tv.accedo.dishonstream2.domain.usecase.home

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tv.accedo.dishonstream2.domain.repository.CMPRepository

class GetPropertyLogoUrlUseCase(
    private val cmpRepository: CMPRepository
) {
    suspend operator fun invoke(): String = withContext(Dispatchers.IO) {
        cmpRepository.getAsset(KEY_PROPERTY_LOGO)
    }

    companion object {
        const val KEY_PROPERTY_LOGO = "propertyLogo"
    }
}