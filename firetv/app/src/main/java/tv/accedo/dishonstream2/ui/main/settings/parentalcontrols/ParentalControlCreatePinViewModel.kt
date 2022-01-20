package tv.accedo.dishonstream2.ui.main.settings.parentalcontrols

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import timber.log.Timber
import tv.accedo.dishonstream2.domain.usecase.settings.parentalcontrols.GetPermittedClassificationsUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.parentalcontrols.SetPermittedClassificationsUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.parentalcontrols.createpin.GetParentalControlPinUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.parentalcontrols.createpin.SetParentalControlPinUseCase
import tv.accedo.dishonstream2.ui.base.BaseViewModel
import tv.accedo.dishonstream2.ui.main.settings.parentalcontrols.permittedclassifications.PermittedClassificationsViewModel

class ParentalControlCreatePinViewModel(
    private val setParentalControlPinUseCase: SetParentalControlPinUseCase,
    private val getParentalControlPinUseCase: GetParentalControlPinUseCase
): BaseViewModel() {
    private val createPinMutableLiveData: MutableLiveData<String> = MutableLiveData()
    val createPinLiveData: LiveData<String> = createPinMutableLiveData

    fun setParentalControlPin(value: String) {
        viewModelScope.launch {
            try {
                setParentalControlPinUseCase(value)
                Timber.tag(PermittedClassificationsViewModel.TAG).i("parental control pin saved: %s", value.toString())
                getParentalControlPin()
            } catch (ex: Exception) {
                Timber.tag(TAG).e(ex)
            }
        }
    }


    private fun getParentalControlPin(){
        viewModelScope.launch {
            try {
                getParentalControlPinUseCase()?.let{
                    Timber.tag(PermittedClassificationsViewModel.TAG).i("parental control pin retrieved: %s", it.toString())
                    createPinMutableLiveData.value=it.toString()
                }
            } catch (ex: Exception) {
                Timber.tag(PermittedClassificationsViewModel.TAG).e("Exception in get parental control pin() : $ex")
            }
        }
    }
    companion object {
        const val TAG = "ParentalControlCreatePinDialog :: "
    }
}