package tv.accedo.dishonstream2.domain.usecase.vod

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import tv.accedo.dishonstream2.domain.model.vod.VODContent
import tv.accedo.dishonstream2.domain.repository.VODRepository

class SearchVODContentUseCase(
    private val vodRepository: VODRepository
) {
    suspend operator fun invoke(searchText: String): List<VODContent> = withContext(Dispatchers.IO) {
        try {
            vodRepository.searchContent(searchText)
        } catch (e: Exception) {
            Timber.e("Exception in SearchVODContentUseCase : ${e.toString()}")
            emptyList()
        }
    }
}