package tv.accedo.dishonstream2.ui.main.tvguide.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import timber.log.Timber
import tv.accedo.dishonstream2.domain.model.supair.Channel
import tv.accedo.dishonstream2.domain.model.supair.Program
import tv.accedo.dishonstream2.domain.model.supair.ProgramInfo
import tv.accedo.dishonstream2.domain.usecase.channel.GetDRMTokenUseCase
import tv.accedo.dishonstream2.domain.usecase.channel.GetEPGUseCase
import tv.accedo.dishonstream2.domain.usecase.player.GetLicenseDetailsUseCase
import tv.accedo.dishonstream2.domain.usecase.program.GetCurrentProgramByChannelIdUseCase
import tv.accedo.dishonstream2.domain.usecase.program.GetProgramInfoUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.GetTimeFormatUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.TimeFormat
import tv.accedo.dishonstream2.domain.usecase.settings.theme.IsLargeFontSizeEnabledUseCase
import tv.accedo.dishonstream2.ui.base.BaseViewModel
import tv.accedo.dishonstream2.ui.main.tvguide.player.model.PlaybackInfo

class PlayerViewModel(
    private val getLicenseDetailsUseCase: GetLicenseDetailsUseCase,
    private val getDRMTokenUseCase: GetDRMTokenUseCase,
    private val getProgramInfoUseCase: GetProgramInfoUseCase,
    private val getCurrentProgramByChannelIdUseCase: GetCurrentProgramByChannelIdUseCase,
    private val getEPGUseCase: GetEPGUseCase,
    private val isLargeFontSizeEnabledUseCase: IsLargeFontSizeEnabledUseCase,
    private val getTimeFormatUseCase: GetTimeFormatUseCase
) : BaseViewModel() {
    private var miniEpgVisible = false

    data class ProgramDetails(
        val channel: Channel,
        val program: Program,
        val programInfo: ProgramInfo?,
        val timeFormat: TimeFormat
    )

    private val _playbackInfo = MutableLiveData<PlaybackInfo?>()
    val playbackInfo: LiveData<PlaybackInfo?> = _playbackInfo

    private val _programDetail = MutableLiveData<ProgramDetails>()
    val programDetail: LiveData<ProgramDetails> = _programDetail

    private val _miniEpgLiveData = MutableLiveData<Pair<Boolean, List<Channel>>>()
    val miniEpgLiveData: LiveData<Pair<Boolean, List<Channel>>> = _miniEpgLiveData

    private val _largeFontEnabledLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    val largeFontEnabledLiveData: LiveData<Boolean> = _largeFontEnabledLiveData

    init {
        getIsLargeFontSizeEnabled()
    }

    fun playContent(channel: Channel) {
        viewModelScope.launch {
            onLoading(true)
            try {
                val program = getCurrentProgramByChannelIdUseCase(channel.serviceKey)
                    ?: throw RuntimeException("Unable to get current program")

                val licenseDetail = getLicenseDetailsUseCase()
                val drm = getDRMTokenUseCase(channel.contentId)
                onLoading(false)
                _playbackInfo.value = PlaybackInfo(
                    playerLicense = licenseDetail.bitmovinLicenceKey,
                    drmLicenseURL = licenseDetail.widewineLicenseUrl,
                    drmToken = drm.drmToken,
                    hlsURL = drm.serviceUrlHLS,
                    dashURL = drm.serviceUrlDASH,
                    program.startTime,
                    program.endTime
                )

                val programInfo = getProgramInfo(program.echoStarId)
                val timeFormat = getTimeFormatUseCase()
                _programDetail.value = ProgramDetails(channel, program, programInfo, timeFormat)
                scheduleProgramDetailsUpdaterJob(channel, program)
            } catch (ex: Exception) {
                onLoading(false)
                onError(ex)
            }
        }
    }

    private suspend fun getProgramInfo(echostarId: String): ProgramInfo? {
        return try {
            getProgramInfoUseCase(echostarId)
        } catch (ex: Exception) {
            null
        }
    }

    fun showMiniEpg() {
        if (miniEpgVisible) return
        viewModelScope.launch {
            try {
                val channels = getEPGUseCase()
                miniEpgVisible = true
                _miniEpgLiveData.value = miniEpgVisible to channels
            } catch (ex: Exception) {
                Timber.e("error getting epg while loading MiniEPG ")
                miniEpgVisible = false
                _miniEpgLiveData.value = miniEpgVisible to emptyList()
            }
        }
    }

    fun hideMiniEpg() {
        if (!miniEpgVisible) return
        miniEpgVisible = false
        _miniEpgLiveData.value = miniEpgVisible to emptyList()
    }

    fun isMiniEpgVisible() = miniEpgVisible

    private fun scheduleProgramDetailsUpdaterJob(channel: Channel, program: Program) {
        viewModelScope.launch {
            val timeLeftForProgramToCompleteInMillis =
                program.endTime - Clock.System.now().toEpochMilliseconds()
            delay(timeLeftForProgramToCompleteInMillis)
            try {
                val currentProgram = getCurrentProgramByChannelIdUseCase(channel.serviceKey)
                    ?: throw RuntimeException("Error while getting current program details")
                val programInfo = getProgramInfo(currentProgram.echoStarId)
                val timeFormat = getTimeFormatUseCase()
                _programDetail.value = ProgramDetails(channel, currentProgram, programInfo, timeFormat)
                scheduleProgramDetailsUpdaterJob(channel, currentProgram)
            } catch (ex: Exception) {
                Timber.e("Error while updating the program details")
            }
        }
    }

    private fun getIsLargeFontSizeEnabled() {
        viewModelScope.launch {
            try {
                val isLargeFontEnabled = isLargeFontSizeEnabledUseCase()
                _largeFontEnabledLiveData.value = isLargeFontEnabled
            } catch (ex: Exception) {
                _largeFontEnabledLiveData.value = false
            }
        }
    }
}