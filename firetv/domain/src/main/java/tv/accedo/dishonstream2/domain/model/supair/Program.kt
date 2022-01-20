package tv.accedo.dishonstream2.domain.model.supair

import android.os.Parcelable
import kotlinx.datetime.Clock
import kotlinx.parcelize.Parcelize
import java.util.concurrent.TimeUnit

@Parcelize
data class Program(
    val name: String,
    val startTime: Long,
    val endTime: Long,
    val duration: Long,
    val echoStarId: String,
    val startTimeFake: Long,
    var channelId: Long = 0L, // Custom field added manually. To access this field make sure its added first
    var channelLogoUrl: String? = null // Custom field added manually. To access this field make sure its added first
) : Parcelable {

    fun isLive(): Boolean {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        return currentTime in startTime until endTime
    }

    fun isLiveOrFuture(): Boolean {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        return isLive() || endTime > currentTime
    }

    fun getRemainingTimeInMillis(): Long {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        return (endTime - currentTime)
    }

    fun getRemainingTimeInMinutes(): Long {
        val remainingTime = getRemainingTimeInMillis()
        val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingTime)
        return if (minutes == 0L) 1 else minutes
    }

    fun getProgramCompletionPercent(): Int {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        return (((currentTime - startTime) * 100) / (endTime - startTime)).toInt()
    }

    fun hasProgramEnded(): Boolean {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        return currentTime > endTime
    }
}