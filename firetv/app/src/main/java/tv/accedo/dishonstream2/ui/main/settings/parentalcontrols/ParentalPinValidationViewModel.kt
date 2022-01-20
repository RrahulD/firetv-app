package tv.accedo.dishonstream2.ui.main.settings.parentalcontrols

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import timber.log.Timber
import tv.accedo.dishonstream2.domain.usecase.settings.parentalcontrols.GetPermittedClassificationsUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.parentalcontrols.createpin.GetParentalPinValidationUseCase
import tv.accedo.dishonstream2.ui.base.BaseViewModel

class ParentalPinValidationViewModel(
    private val getParentalPinValidationUseCase: GetParentalPinValidationUseCase
): BaseViewModel() {
    private val parentalPinValidationMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val parentalPinValidationLiveData: LiveData<Boolean> = parentalPinValidationMutableLiveData


    fun validateParentalPin(enteredPin : String, reenteredPin:String) {
        viewModelScope.launch {
            try {
                 getParentalPinValidationUseCase(enteredPin, reenteredPin)?.let {
                    parentalPinValidationMutableLiveData.value=it
                     Timber.tag(TAG).i("set validateParentalPin : %s",it)
                }

            } catch (ex: Exception) {
                Timber.tag(ParentalPinValidationViewModel.TAG).e("Exception in validateParentalPin() : $ex")
            }
        }
    }

    companion object {
        const val TAG = "ParentalPinDialog :: "
    }
}