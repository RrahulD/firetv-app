package tv.accedo.dishonstream2.domain.usecase.vod

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import tv.accedo.dishonstream2.domain.model.vod.EpisodeDetail
import tv.accedo.dishonstream2.domain.repository.VODRepository
import tv.accedo.dishonstream2.domain.util.AppConstants

class GetSeasonDetailsCase(
    private val vodRepository: VODRepository
) {
    suspend operator fun invoke(showId: String, seasonNumber: Int): List<EpisodeDetail> = withContext(Dispatchers.IO) {
        try {
            vodRepository.getSeasonDetails(showId, seasonNumber)
        } catch (e: Exception) {
            Timber.tag(AppConstants.OnDemand.TAG).e("Exception in GetSeasonDetailsCase : ${e.toString()}")
            emptyList()
        }
    }
}