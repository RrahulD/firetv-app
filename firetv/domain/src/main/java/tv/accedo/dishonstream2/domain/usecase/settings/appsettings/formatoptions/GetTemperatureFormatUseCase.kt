package tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tv.accedo.dishonstream2.domain.storage.KeyValueStore

enum class TemperatureFormat(val value: String) {
    CELSIUS("°C"),
    FAHRENHEIT("°F")
}

class GetTemperatureFormatUseCase(
    private val keyValueStore: KeyValueStore
) {
    suspend operator fun invoke(): TemperatureFormat = withContext(Dispatchers.IO) {
        val temperatureFormatString = keyValueStore.readString(TEMPERATURE_FORMAT_KEY, TemperatureFormat.FAHRENHEIT.value)
        TemperatureFormat.values().find { it.value == temperatureFormatString } ?: TemperatureFormat.FAHRENHEIT
    }

    companion object {
        const val TEMPERATURE_FORMAT_KEY = "temperatureFormat"
    }
}