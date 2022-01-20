package tv.accedo.dishonstream2.domain.usecase.player

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import tv.accedo.dishonstream2.domain.model.player.LicenseDetail
import tv.accedo.dishonstream2.domain.repository.CMPRepository

class GetLicenseDetailsUseCase(
    private val cmpRepository: CMPRepository
) {
    suspend operator fun invoke(): LicenseDetail = withContext(Dispatchers.IO) {
        val licenseJson = JSONObject(cmpRepository.getMetadata(LICENCES_KEY))
        LicenseDetail(licenseJson.getString(BITMOVIN_LICENCE_KEY), licenseJson.getString(WIDEVINE_LICENSE_URL_KEY))
    }

    companion object {
        private const val LICENCES_KEY = "licences"
        private const val BITMOVIN_LICENCE_KEY = "bitmovinLicenseKey"
        private const val WIDEVINE_LICENSE_URL_KEY = "widevineLicenseURL"
    }
}