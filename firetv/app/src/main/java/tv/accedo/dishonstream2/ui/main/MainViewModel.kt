package tv.accedo.dishonstream2.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import timber.log.Timber
import tv.accedo.dishonstream2.domain.usecase.home.GetHomePageBackgroundUrlUseCase
import tv.accedo.dishonstream2.domain.usecase.home.GetHomePageTabsUseCase
import tv.accedo.dishonstream2.domain.usecase.home.GetPropertyLogoUrlUseCase
import tv.accedo.dishonstream2.domain.usecase.home.Tab
import tv.accedo.dishonstream2.ui.base.BaseViewModel

class MainViewModel(
    private val getHomePageBackgroundUseCase: GetHomePageBackgroundUrlUseCase,
    private val getPropertyLogoUrlUseCase: GetPropertyLogoUrlUseCase,
    private val getHomePageTabsUseCase: GetHomePageTabsUseCase,
) : BaseViewModel() {

    private val _landingPageBackgroundUrlLiveData: MutableLiveData<String> = MutableLiveData()
    val landingPageBackgroundUrlLiveData: LiveData<String> = _landingPageBackgroundUrlLiveData

    private val _propertyLogoLiveData: MutableLiveData<String> = MutableLiveData()
    val propertyLogoLiveData: LiveData<String> = _propertyLogoLiveData

    private val _landingPageTabsLiveData: MutableLiveData<List<Tab>> = MutableLiveData()
    val landingPageTabsLiveData: LiveData<List<Tab>> = _landingPageTabsLiveData

    init {
        getNavigationLogo()
        getLandingPageTabs()
    }

    fun getLandingPageBackground() {
        viewModelScope.launch {
            try {
                val landingPageBackground = getHomePageBackgroundUseCase()
                _landingPageBackgroundUrlLiveData.value = landingPageBackground
            } catch (ex: Exception) {
                onError(ex)
            }
        }
    }

    private fun getNavigationLogo() {
        viewModelScope.launch {
            try {
                _propertyLogoLiveData.value = getPropertyLogoUrlUseCase()
            } catch (ex: Exception) {
                Timber.e("Unable to get property logo")
            }
        }
    }

    private fun getLandingPageTabs() {
        viewModelScope.launch {
            try {
                val landingPageTabs = getHomePageTabsUseCase()
                _landingPageTabsLiveData.value = landingPageTabs
            } catch (ex: Exception) {
                onError(ex)
            }
        }
    }

}