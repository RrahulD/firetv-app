package tv.accedo.dishonstream2.domain.usecase.splash

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tv.accedo.dishonstream2.domain.repository.CMPRepository
import tv.accedo.dishonstream2.domain.repository.DishRepository
import tv.accedo.dishonstream2.domain.repository.DishSmartBoxRepository
import tv.accedo.dishonstream2.domain.repository.VODRepository

class InitializeAppUseCase(
    private val dishSmartBoxRepository: DishSmartBoxRepository,
    private val cmpRepository: CMPRepository,
    private val dishRepository: DishRepository
) {
    suspend operator fun invoke(): Boolean = withContext(Dispatchers.IO) {
        val smartBoxId = dishSmartBoxRepository.getSmartBoxIdentifier()
        cmpRepository.initialize(smartBoxId)
        val endpoints = cmpRepository.getEndpoints()
        dishRepository.initializeEndpoints(endpoints)
        true
    }
}