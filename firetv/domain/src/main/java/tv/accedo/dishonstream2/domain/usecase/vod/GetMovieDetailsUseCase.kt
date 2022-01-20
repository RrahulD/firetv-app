package tv.accedo.dishonstream2.domain.usecase.vod

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import tv.accedo.dishonstream2.domain.model.vod.MovieDetails
import tv.accedo.dishonstream2.domain.repository.VODRepository
import tv.accedo.dishonstream2.domain.util.AppConstants

class GetMovieDetailsUseCase(
    private val vodRepository: VODRepository
) {
    suspend operator fun invoke(movieId: String): MovieDetails? = withContext(Dispatchers.IO) {
        try {
            vodRepository.getMovieDetails(movieId)
        } catch (e: Exception) {
            Timber.tag(AppConstants.OnDemand.TAG).e("Exception in GetMovieDetailsUseCase : ${e.toString()}")
            null
        }
    }
}