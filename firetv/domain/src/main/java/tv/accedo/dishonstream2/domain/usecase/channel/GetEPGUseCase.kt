package tv.accedo.dishonstream2.domain.usecase.channel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import tv.accedo.dishonstream2.domain.model.supair.Channel
import tv.accedo.dishonstream2.domain.repository.DishRepository

class GetEPGUseCase(private val repository: DishRepository) {
    suspend operator fun invoke(): List<Channel> = withContext(Dispatchers.IO) {
        try {
            repository.getEpg()
        } catch (e: Exception) {
            Timber.e("Exception in GetEPGUseCase : ${e.toString()}")
            emptyList<Channel>()
        }
    }
}