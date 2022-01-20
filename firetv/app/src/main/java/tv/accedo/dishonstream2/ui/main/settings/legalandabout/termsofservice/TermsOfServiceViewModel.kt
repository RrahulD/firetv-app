package tv.accedo.dishonstream2.ui.main.settings.legalandabout.termsofservice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tv.accedo.dishonstream2.domain.usecase.settings.legalandabout.GetTermsOfServiceUseCase
import tv.accedo.dishonstream2.ui.base.BaseViewModel

class TermsOfServiceViewModel(private val getTermsOfServiceUseCase: GetTermsOfServiceUseCase) : BaseViewModel() {

    private val _termsOfServiceLiveData: MutableLiveData<String> = MutableLiveData()
    val termsOfServiceLiveData: LiveData<String> = _termsOfServiceLiveData

    init {
        getTermsOfService()
    }

    fun getTermsOfService() {
        viewModelScope.launch {
            try {
                onLoading(true)
                val termsOfService = getTermsOfServiceUseCase()
                _termsOfServiceLiveData.value = termsOfService
            } catch (ex: Exception) {
                onLoading(false)
                onError(ex)
            }
        }
    }
}