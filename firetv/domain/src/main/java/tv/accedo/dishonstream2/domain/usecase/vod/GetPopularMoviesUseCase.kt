package tv.accedo.dishonstream2.domain.usecase.vod

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import tv.accedo.dishonstream2.domain.model.vod.Movie
import tv.accedo.dishonstream2.domain.repository.VODRepository
import tv.accedo.dishonstream2.domain.util.AppConstants

class GetPopularMoviesUseCase(
    private val vodRepository: VODRepository
) {
    suspend operator fun invoke(count: Int, page: Int): List<Movie> = withContext(Dispatchers.IO) {
        try {
            vodRepository.getPopularMovies(count, page)
        } catch (e: Exception) {
            Timber.tag(AppConstants.OnDemand.TAG).e("Exception in GetPopularMoviesUseCase : ${e.toString()}")
            emptyList()
        }
    }
}