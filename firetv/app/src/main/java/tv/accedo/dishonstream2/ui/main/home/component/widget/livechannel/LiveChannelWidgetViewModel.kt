package tv.accedo.dishonstream2.ui.main.home.component.widget.livechannel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import timber.log.Timber
import tv.accedo.dishonstream2.domain.model.supair.Channel
import tv.accedo.dishonstream2.domain.model.supair.Program
import tv.accedo.dishonstream2.domain.model.supair.ProgramInfo
import tv.accedo.dishonstream2.domain.usecase.channel.GetChannelByChannelIdUseCase
import tv.accedo.dishonstream2.domain.usecase.program.GetCurrentProgramByChannelUseCase
import tv.accedo.dishonstream2.domain.usecase.program.GetProgramInfoUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.parentalcontrols.GetPermittedClassificationsUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.parentalcontrols.createpin.GetParentalControlPinUseCase
import tv.accedo.dishonstream2.ui.base.resource.Resource
import tv.accedo.dishonstream2.ui.base.viewholder.BaseViewHolderViewModel
import tv.accedo.dishonstream2.ui.main.settings.parentalcontrols.permittedclassifications.PermittedClassificationsViewModel
import java.util.concurrent.TimeUnit

@OptIn(KoinApiExtension::class)
class LiveChannelWidgetViewModel(
    private val getChannelByChannelIdUseCase: GetChannelByChannelIdUseCase,
    private val getCurrentProgramByChannelUseCase: GetCurrentProgramByChannelUseCase,
    private val getProgramInfoUseCase: GetProgramInfoUseCase,
    override val viewHolderScope: CoroutineScope
) : BaseViewHolderViewModel, KoinComponent {

    private var remainingTimeUpdaterJob: Job? = null
    private var channelId: Long? = null

    data class ProgramProgress(
        val remainingTime: Long,
        val completionPercent: Int
    )

    data class CurrentProgramDetail(
        val channel: Channel,
        val program: Program,
        val programInfo: ProgramInfo?
    )

    private val _liveChannelCurrentProgramDetails = MutableLiveData<Resource<CurrentProgramDetail>>()
    val liveChannelCurrentProgramDetails: LiveData<Resource<CurrentProgramDetail>> =
        _liveChannelCurrentProgramDetails

    private val _programProgressLiveData = MutableLiveData<ProgramProgress>()
    val programProgressLiveData: LiveData<ProgramProgress> = _programProgressLiveData

    fun loadCurrentProgramDetails(channelId: Long) {
        this.channelId = channelId
        viewHolderScope.launch {
            _liveChannelCurrentProgramDetails.value = Resource.Loading()
            _liveChannelCurrentProgramDetails.value = try {
                val channel = getChannelByChannelIdUseCase(channelId)

                if (channel != null) {
                    val program = getCurrentProgramByChannelUseCase(channel)
                    if (program != null && program.echoStarId.isNotBlank()) {
                        val programDetail = getProgramInfo(program.echoStarId)
                        scheduleRemainingTimeUpdaterJob(program)
                        Resource.Success(CurrentProgramDetail(channel, program, programDetail))
                    } else
                        Resource.Failure(RuntimeException("Program not found!"))
                } else
                    Resource.Failure(RuntimeException("Channel not found"))
            } catch (ex: Exception) {
                ex.printStackTrace()
                Resource.Failure(ex)
            }
        }
    }

    private suspend fun getProgramInfo(echoStarId: String): ProgramInfo? {
        return try {
            getProgramInfoUseCase(echoStarId)
        } catch (ex: Exception) {
            null
        }
    }

    private fun scheduleRemainingTimeUpdaterJob(program: Program) {
        remainingTimeUpdaterJob?.let { if (it.isActive) it.cancel() }

        remainingTimeUpdaterJob = viewHolderScope.launch {
            while (isActive) {
                val remainingTimeInMillis = program.getRemainingTimeInMillis()
                if (remainingTimeInMillis < 0) {
                    // if the program is already over, cancel this updater job and refresh the program info
                    this.cancel()
                    channelId?.let { loadCurrentProgramDetails(it) }
                } else {
                    _programProgressLiveData.value = ProgramProgress(
                        program.getRemainingTimeInMinutes(),
                        program.getProgramCompletionPercent()
                    )
                    val oneMinute = TimeUnit.MINUTES.toMillis(1)
                    val refreshDelayTime = if (remainingTimeInMillis >= oneMinute)
                        oneMinute else remainingTimeInMillis
                    delay(refreshDelayTime)
                }
            }
        }
    }
}