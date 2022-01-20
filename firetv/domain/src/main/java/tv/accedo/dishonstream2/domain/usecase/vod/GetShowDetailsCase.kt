package tv.accedo.dishonstream2.domain.usecase.vod

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import tv.accedo.dishonstream2.domain.model.vod.ShowDetails
import tv.accedo.dishonstream2.domain.repository.VODRepository
import tv.accedo.dishonstream2.domain.util.AppConstants

class GetShowDetailsCase(
    private val vodRepository: VODRepository
) {
    suspend operator fun invoke(showId: String): ShowDetails? = withContext(Dispatchers.IO) {
        try {
            vodRepository.getShowDetails(showId)
        } catch (e: Exception) {
            Timber.tag(AppConstants.OnDemand.TAG).e("Exception in GetShowDetailsCase : ${e.toString()}")
            null
        }
    }
}