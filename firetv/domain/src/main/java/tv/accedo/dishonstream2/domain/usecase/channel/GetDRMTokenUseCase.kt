package tv.accedo.dishonstream2.domain.usecase.channel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tv.accedo.dishonstream2.domain.model.supair.DRM
import tv.accedo.dishonstream2.domain.repository.DishRepository

class GetDRMTokenUseCase(
    private val dishRepository: DishRepository
) {
    suspend operator fun invoke(contentID: String): DRM = withContext(Dispatchers.IO) {
        dishRepository.getDRMToken(contentID)
    }
}