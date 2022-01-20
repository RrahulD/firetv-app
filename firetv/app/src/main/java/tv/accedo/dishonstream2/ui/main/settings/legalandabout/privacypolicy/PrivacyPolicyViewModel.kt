package tv.accedo.dishonstream2.ui.main.settings.legalandabout.privacypolicy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tv.accedo.dishonstream2.domain.usecase.settings.legalandabout.GetPrivacyPolicyUseCase
import tv.accedo.dishonstream2.ui.base.BaseViewModel

class PrivacyPolicyViewModel(
    private val getPrivacyPolicyUseCase: GetPrivacyPolicyUseCase
) : BaseViewModel() {

    private val _privacyPolicyLiveData: MutableLiveData<String> = MutableLiveData()
    val privacyPolicyLiveData: LiveData<String> = _privacyPolicyLiveData

    init {
        getPrivacyPolicy()
    }

    private fun getPrivacyPolicy() {
        viewModelScope.launch {
            try {
                onLoading(true)
                val privacyPolicy = getPrivacyPolicyUseCase()
                onLoading(false)
                _privacyPolicyLiveData.value = privacyPolicy
            } catch (ex: Exception) {
                onLoading(false)
                onError(ex)
            }
        }
    }
}

