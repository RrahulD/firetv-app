package tv.accedo.dishonstream2.ui.main.tvguide

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import timber.log.Timber
import tv.accedo.dishonstream2.domain.model.supair.Channel
import tv.accedo.dishonstream2.domain.model.supair.Program
import tv.accedo.dishonstream2.domain.usecase.channel.GetDRMTokenUseCase
import tv.accedo.dishonstream2.domain.usecase.channel.GetEPGUseCase
import tv.accedo.dishonstream2.domain.usecase.player.GetLicenseDetailsUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.tvguide.GetTVGuideStyleUseCase
import tv.accedo.dishonstream2.ui.base.BaseViewModel
import tv.accedo.dishonstream2.ui.main.tvguide.TVGuideFragment.Companion.CLASSIC
import tv.accedo.dishonstream2.ui.main.tvguide.player.model.PlaybackInfo
import java.util.concurrent.TimeUnit

class TVGuideViewModel(
    private val getLicenseDetailsUseCase: GetLicenseDetailsUseCase,
    private val getDRMTokenUseCase: GetDRMTokenUseCase,
    private val getEPGUseCase: GetEPGUseCase,
    private val getTVGuideStyleUseCaseV2: GetTVGuideStyleUseCase,
) : BaseViewModel() {

    private var classicEpgScrollerJob: Job? = null

    private val tvGuideStyleLiveData: MutableLiveData<String> = MutableLiveData()
    private val useLightThemeLiveData: MutableLiveData<Boolean> = MutableLiveData(false)

    private val _epgLiveData = MutableLiveData<List<Channel>>()
    val epgLiveData: LiveData<List<Channel>> = _epgLiveData

    private val _pipPlaybackInfo = MutableLiveData<PlaybackInfo>()
    val pipPlaybackInfo: LiveData<PlaybackInfo> = _pipPlaybackInfo

    private val _scrollClassicEpgLiveData = MutableLiveData<Pair<Int, Int>>()
    val scrollClassicEpgLiveData: LiveData<Pair<Int, Int>> = _scrollClassicEpgLiveData

    fun getEpg() {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            onLoading(false)
            onError(exception)
        }

        viewModelScope.launch(exceptionHandler) {
            onLoading(true)
            val tvGuideStyleDeferred = async { getTVGuideStyleUseCaseV2() }
            val epgDeferred = async { getEPGUseCase() }

            tvGuideStyleLiveData.value = tvGuideStyleDeferred.await()
            val epgData = epgDeferred.await()
            onLoading(false)
            if (epgData.isNotEmpty()) {
                _epgLiveData.value = epgData
                // in case of classic epg, schedule a job which shifts the epg by every 30 mins
                if (tvGuideStyleLiveData.value == CLASSIC)
                    scheduleClassicEpgScrollerJob()
            } else
                onError(RuntimeException("Error while loading the TV Guide!"))
        }
    }

    fun playContentInPip(channel: Channel, program: Program) {
        viewModelScope.launch {
            onLoading(true)
            try {
                val licenseDetail = getLicenseDetailsUseCase()
                val drm = getDRMTokenUseCase(channel.contentId)
                onLoading(false)
                _pipPlaybackInfo.value = PlaybackInfo(
                    playerLicense = licenseDetail.bitmovinLicenceKey,
                    drmLicenseURL = licenseDetail.widewineLicenseUrl,
                    drmToken = drm.drmToken,
                    hlsURL = drm.serviceUrlHLS,
                    dashURL = drm.serviceUrlDASH,
                    program.startTime,
                    program.endTime
                )
            } catch (ex: Exception) {
                onLoading(false)
                onError(ex)
            }
        }
    }

    // schedules the job which runs on every 30 min slot of classic EPG to shift it right to next 30 min slot
    private fun scheduleClassicEpgScrollerJob() {
        try {
            // cancel the job, if its already running
            classicEpgScrollerJob?.cancel()
            classicEpgScrollerJob = viewModelScope.launch {
                while (isActive) {
                    val thirtyMinutes = TimeUnit.MINUTES.toMillis(30)
                    val timeNow = Clock.System.now().toEpochMilliseconds()
                    val timeToDelayTheScroll = thirtyMinutes - (timeNow % thirtyMinutes)
                    delay(timeToDelayTheScroll)
                    _scrollClassicEpgLiveData.value = Pair(1, 0)
                }
            }
        } catch (ex: Exception) {
            Timber.e("Exception thrown while scheduling the classic EPG scroller job.")
            Timber.e(ex)
        }
    }

    fun styleEPG(): LiveData<String> {
        return tvGuideStyleLiveData
    }

    fun useLightTheme(): LiveData<Boolean> {
        return useLightThemeLiveData
    }
}