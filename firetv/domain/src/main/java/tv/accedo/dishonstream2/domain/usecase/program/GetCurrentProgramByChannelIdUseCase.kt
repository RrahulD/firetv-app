package tv.accedo.dishonstream2.domain.usecase.program

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tv.accedo.dishonstream2.domain.model.supair.Program
import tv.accedo.dishonstream2.domain.usecase.channel.GetChannelByChannelIdUseCase

class GetCurrentProgramByChannelIdUseCase(
    private val getChannelByChannelIdUseCase: GetChannelByChannelIdUseCase,
    private val getCurrentProgramByChannelUseCase: GetCurrentProgramByChannelUseCase
) {
    suspend operator fun invoke(channelId: Long): Program? = withContext(Dispatchers.IO) {
        getChannelByChannelIdUseCase(channelId)?.let { channel -> getCurrentProgramByChannelUseCase(channel) }
    }
}