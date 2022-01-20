package tv.accedo.dishonstream2.domain.usecase.home

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tv.accedo.dishonstream2.domain.model.home.weather.Weather
import tv.accedo.dishonstream2.domain.repository.CMPRepository
import tv.accedo.dishonstream2.domain.repository.DishRepository

class GetWeatherDetailUseCase(
    private val dishRepository: DishRepository,
    private val cmpRepository: CMPRepository
) {

    suspend operator fun invoke(): Weather = withContext(Dispatchers.IO) {
        val zipCode = cmpRepository.getMetadata(KEY_ZIPCODE)
        dishRepository.getWeatherDetail(zipCode)
    }

    companion object {
        private const val KEY_ZIPCODE = "zipCode"
    }
}