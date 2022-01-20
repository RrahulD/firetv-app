package tv.accedo.dishonstream2.domain.usecase.vod

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import tv.accedo.dishonstream2.domain.model.vod.Show
import tv.accedo.dishonstream2.domain.repository.VODRepository
import tv.accedo.dishonstream2.domain.util.AppConstants

class GetPopularShowsUseCase(
    private val vodRepository: VODRepository
) {
    suspend operator fun invoke(count: Int, page: Int): List<Show> = withContext(Dispatchers.IO) {
        try {
            vodRepository.getPopularShows(count, page)
        } catch (e: Exception) {
            Timber.tag(AppConstants.OnDemand.TAG).e("Exception in GetPopularShowsUseCase : ${e.toString()}")
            emptyList()
        }
    }
}