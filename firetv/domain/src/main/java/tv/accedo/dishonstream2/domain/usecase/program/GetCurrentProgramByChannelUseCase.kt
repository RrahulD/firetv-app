package tv.accedo.dishonstream2.domain.usecase.program

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import tv.accedo.dishonstream2.domain.model.supair.Channel
import tv.accedo.dishonstream2.domain.model.supair.Program

class GetCurrentProgramByChannelUseCase {
    suspend operator fun invoke(channel: Channel): Program? = withContext(Dispatchers.Default) {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        channel.programs.find { currentTime >= it.startTime && currentTime <= it.endTime }
    }
}