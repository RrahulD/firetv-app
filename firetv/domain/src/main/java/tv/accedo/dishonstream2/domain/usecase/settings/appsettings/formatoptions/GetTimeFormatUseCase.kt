package tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tv.accedo.dishonstream2.domain.storage.KeyValueStore

enum class TimeFormat(val value: String) {
    FORMAT_12_HOUR("12 HOUR"),
    FORMAT_24_HOUR("24 HOUR")
}

class GetTimeFormatUseCase(
    private val keyValueStore: KeyValueStore
) {
    suspend operator fun invoke(): TimeFormat = withContext(Dispatchers.IO) {
        val timeFormatString = keyValueStore.readString(TIME_FORMAT_KEY, TimeFormat.FORMAT_12_HOUR.value)
        TimeFormat.values().find { it.value == timeFormatString } ?: TimeFormat.FORMAT_12_HOUR
    }

    companion object {
        const val TIME_FORMAT_KEY = "timeFormat"
    }
}