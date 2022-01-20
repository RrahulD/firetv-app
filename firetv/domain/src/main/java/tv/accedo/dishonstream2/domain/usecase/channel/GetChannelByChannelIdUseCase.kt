package tv.accedo.dishonstream2.domain.usecase.channel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tv.accedo.dishonstream2.domain.model.supair.Channel
import tv.accedo.dishonstream2.domain.repository.DishRepository

class GetChannelByChannelIdUseCase(
    private val dishRepository: DishRepository
) {
    suspend operator fun invoke(channelId: Long): Channel? = withContext(Dispatchers.IO) {
        dishRepository.getChannelById(channelId)
    }
}