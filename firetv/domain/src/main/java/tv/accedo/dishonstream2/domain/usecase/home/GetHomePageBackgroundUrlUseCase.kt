package tv.accedo.dishonstream2.domain.usecase.home

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tv.accedo.dishonstream2.domain.repository.CMPRepository

class GetHomePageBackgroundUrlUseCase(
    private val cmpRepository: CMPRepository
) {

    suspend operator fun invoke(): String = withContext(Dispatchers.IO) {
        cmpRepository.getAsset(BACKGROUND_KEY)
    }

    companion object {
        const val BACKGROUND_KEY = "background"
    }
}
