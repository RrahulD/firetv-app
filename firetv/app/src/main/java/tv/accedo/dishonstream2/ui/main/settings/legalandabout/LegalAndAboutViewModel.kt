package tv.accedo.dishonstream2.ui.main.settings.legalandabout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tv.accedo.dishonstream2.domain.usecase.settings.legalandabout.GetAppVersionUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.legalandabout.GetContactUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.legalandabout.GetDeviceInformationUseCase
import tv.accedo.dishonstream2.ui.base.BaseViewModel

class LegalAndAboutViewModel(
    private val getAppVersionUseCase: GetAppVersionUseCase,
    private val getContactUseCase: GetContactUseCase,
    private val getDeviceInformationUseCase: GetDeviceInformationUseCase
) : BaseViewModel() {

    private val _appVersionLiveData: MutableLiveData<String> = MutableLiveData()
    val appVersionLiveData: LiveData<String> = _appVersionLiveData

    private val _deviceInformationLiveData: MutableLiveData<String> = MutableLiveData()
    val deviceInformationLiveData: LiveData<String> = _deviceInformationLiveData

    private val _contactLiveData: MutableLiveData<String> = MutableLiveData()
    val contactLiveData: LiveData<String> = _contactLiveData

    init {
        getAppVersion()
        getContact()
        getDeviceInfo()
    }

    private fun getDeviceInfo() {
        viewModelScope.launch {
            try {
                val deviceInfo = getDeviceInformationUseCase()
                _deviceInformationLiveData.postValue(deviceInfo)
            } catch (ex: Exception) {
                onError(ex)
            }
        }
    }

    private fun getAppVersion() {
        viewModelScope.launch {
            try {
                val appVersion = getAppVersionUseCase()
                _appVersionLiveData.value = appVersion
            } catch (ex: Exception) {
                onError(ex)
            }
        }
    }

    private fun getContact() {
        viewModelScope.launch {
            try {
                val contact = getContactUseCase()
                _contactLiveData.value = contact
            } catch (ex: Exception) {
                onError(ex)
            }
        }
    }
}