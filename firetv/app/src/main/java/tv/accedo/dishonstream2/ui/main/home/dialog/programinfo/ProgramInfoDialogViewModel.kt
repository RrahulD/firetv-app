package tv.accedo.dishonstream2.ui.main.home.dialog.programinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import timber.log.Timber
import tv.accedo.dishonstream2.domain.model.supair.ProgramInfo
import tv.accedo.dishonstream2.domain.usecase.program.GetProgramInfoUseCase

class ProgramInfoDialogViewModel(
    private val getProgramInfoUseCase: GetProgramInfoUseCase
) : ViewModel() {

    private val _programDetailsLiveData = MutableLiveData<ProgramInfo?>()
    val programDetailsLiveData: LiveData<ProgramInfo?> = _programDetailsLiveData

    fun loadProgramDetails(echostarId: String) {
        viewModelScope.launch {
            try {
                val programInfo = getProgramInfoUseCase(echostarId)
                _programDetailsLiveData.value = programInfo
            } catch (ex: Exception) {
                _programDetailsLiveData.value = null
                Timber.e("Error while loading program details in the Programinfo dialog.")
            }
        }
    }
}