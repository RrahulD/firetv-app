package tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tv.accedo.dishonstream2.domain.storage.KeyValueStore
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.GetTemperatureFormatUseCase.Companion.TEMPERATURE_FORMAT_KEY

class SetTemperatureFormatUseCase(
    private val keyValueStore: KeyValueStore
) {
    suspend operator fun invoke(format: TemperatureFormat) = withContext(Dispatchers.IO) {
        keyValueStore.writeString(TEMPERATURE_FORMAT_KEY, format.value)
    }
}