package tv.accedo.dishonstream2.domain.usecase.vod

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import tv.accedo.dishonstream2.domain.model.vod.EpisodeDetail
import tv.accedo.dishonstream2.domain.repository.VODRepository

class GetEpisodeDetailsUseCase(
    private val vodRepository: VODRepository
) {
    suspend operator fun invoke(showId: String, seasonNo: Int, episodeNo: Int): EpisodeDetail? = withContext(Dispatchers.IO) {
        try {
            vodRepository.getEpisodeDetails(showId, seasonNo, episodeNo)
        } catch (e: Exception) {
            Timber.e("Exception in GetEpisodeDetailsUseCase : ${e.toString()}")
            null
        }
    }
}