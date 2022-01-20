package tv.accedo.dishonstream2.ui.main.tvguide.player.view.miniepg.program

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import tv.accedo.dishonstream2.domain.model.supair.ProgramInfo
import tv.accedo.dishonstream2.domain.usecase.program.GetProgramInfoUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.GetTimeFormatUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.TimeFormat
import tv.accedo.dishonstream2.ui.base.resource.Resource
import tv.accedo.dishonstream2.ui.base.viewholder.BaseViewHolderViewModel

class MiniEpgProgramViewHolderViewModel(
    private val getProgramInfoUseCase: GetProgramInfoUseCase,
    private val getTimeFormatUseCase: GetTimeFormatUseCase,
    override val viewHolderScope: CoroutineScope
) : BaseViewHolderViewModel {

    init {
        getTimeFormat()
    }

    private val _timeFormatLiveData = MutableLiveData<TimeFormat>()
    val timeFormatLiveData: LiveData<TimeFormat> = _timeFormatLiveData

    private val _programDetailLiveData = MutableLiveData<Resource<ProgramInfo>>()
    val programDetailLiveData: LiveData<Resource<ProgramInfo>> = _programDetailLiveData

    private fun getTimeFormat() {
        viewHolderScope.launch {
            try {
                val timeFormat = getTimeFormatUseCase()
                _timeFormatLiveData.value = timeFormat
            } catch (ex: Exception) {
                _timeFormatLiveData.value = TimeFormat.FORMAT_12_HOUR
            }
        }
    }

    fun loadProgramDetails(echostarId: String) {
        viewHolderScope.launch {
            try {
                _programDetailLiveData.value = Resource.Loading()
                val programInfo = getProgramInfoUseCase(echostarId)
                _programDetailLiveData.value =
                    Resource.Success(programInfo)
            } catch (ex: Exception) {
                _programDetailLiveData.value = Resource.Failure(ex)
            }
        }
    }
}