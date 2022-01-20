package tv.accedo.dishonstream2.domain.usecase.vod

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import tv.accedo.dishonstream2.domain.model.vod.VODContent
import tv.accedo.dishonstream2.domain.repository.VODRepository

class GetTrendingContentsUseCase(
    private val vodRepository: VODRepository
) {
    suspend operator fun invoke(count: Int = 30, page: Int = 0): List<VODContent> = withContext(Dispatchers.IO) {
        try {
            vodRepository.getTrendingContents(count, page)
        } catch (e: Exception) {
            Timber.e("Exception in GetTrendingContentsUseCase : ${e.toString()}")
            emptyList()
        }
    }
}