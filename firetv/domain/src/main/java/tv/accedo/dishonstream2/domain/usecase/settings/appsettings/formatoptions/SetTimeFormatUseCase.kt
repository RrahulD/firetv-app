package tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tv.accedo.dishonstream2.domain.storage.KeyValueStore
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.GetTimeFormatUseCase.Companion.TIME_FORMAT_KEY

class SetTimeFormatUseCase(
    private val keyValueStore: KeyValueStore
) {
    suspend operator fun invoke(format: TimeFormat) = withContext(Dispatchers.IO) {
        keyValueStore.writeString(TIME_FORMAT_KEY, format.value)
    }
}