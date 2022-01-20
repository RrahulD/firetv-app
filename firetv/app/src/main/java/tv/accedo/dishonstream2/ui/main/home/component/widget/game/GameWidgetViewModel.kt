package tv.accedo.dishonstream2.ui.main.home.component.widget.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.GetTimeFormatUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.TimeFormat
import tv.accedo.dishonstream2.ui.base.viewholder.BaseViewHolderViewModel

class GameWidgetViewModel(
    private val getTimeFormatUseCase: GetTimeFormatUseCase,
    override val viewHolderScope: CoroutineScope
) : BaseViewHolderViewModel {

    private val _timeFormatLiveData = MutableLiveData<TimeFormat>()
    val timeFormatLiveData: LiveData<TimeFormat> = _timeFormatLiveData

    init {
        getTimeFormat()
    }

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
}