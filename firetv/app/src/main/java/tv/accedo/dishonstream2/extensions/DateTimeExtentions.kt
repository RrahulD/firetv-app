package tv.accedo.dishonstream2.extensions

import kotlinx.datetime.Instant
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.TimeFormat
import java.text.SimpleDateFormat
import java.util.*

fun getTimeFormatter(timeFormat: TimeFormat, timeZone: TimeZone): SimpleDateFormat {
    val timeFormatter = if (timeFormat == TimeFormat.FORMAT_12_HOUR) SimpleDateFormat("hh:mm aa", Locale.getDefault())
    else SimpleDateFormat("HH:mm", Locale.getDefault())
    timeFormatter.timeZone = timeZone
    return timeFormatter
}

fun getTimeWithDateFormatter(timeFormat: TimeFormat, timeZone: TimeZone): SimpleDateFormat {
    val timeFormatter = if (timeFormat == TimeFormat.FORMAT_12_HOUR) SimpleDateFormat("hh:mm aa MM/dd", Locale.getDefault())
    else SimpleDateFormat("HH:mm MM/dd", Locale.getDefault())
    timeFormatter.timeZone = timeZone
    return timeFormatter
}

fun Instant.getTimeString(format: TimeFormat, timeZone: TimeZone = TimeZone.getDefault()): String {
    return getTimeFormatter(format, timeZone).format(toEpochMilliseconds())
}

fun Instant.getTimeWithDateString(format: TimeFormat, timeZone: TimeZone = TimeZone.getDefault()): String {
    return getTimeWithDateFormatter(format, timeZone).format(toEpochMilliseconds())
}
