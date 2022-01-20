package tv.accedo.dishonstream2.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import timber.log.Timber
import tv.accedo.dishonstream2.data.repository.caching.CacheStore
import tv.accedo.dishonstream2.domain.usecase.channel.GetEPGUseCase
import tv.accedo.dishonstream2.domain.usecase.home.GetHomeTemplateTypeUseCase
import tv.accedo.dishonstream2.domain.usecase.home.GetPropertyLogoUrlUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.theme.GetThemingOptionsUseCase
import tv.accedo.dishonstream2.domain.usecase.splash.InitializeAppUseCase
import tv.accedo.dishonstream2.ui.base.BaseViewModel
import tv.accedo.dishonstream2.ui.theme.ThemeManager

class SplashViewModel(
    private val getPropertyLogoUrlUseCase: GetPropertyLogoUrlUseCase,
    private val initializeAppUseCase: InitializeAppUseCase,
    private val getEPGUseCase: GetEPGUseCase,
    private val getThemingOptionsUseCase: GetThemingOptionsUseCase,
    private val getHomeTemplateTypeUseCase: GetHomeTemplateTypeUseCase,
    private val themeManager: ThemeManager,
    private val cacheStore: CacheStore
) : BaseViewModel() {

    private val _appInitStateLivedata: MutableLiveData<Boolean> = MutableLiveData()
    val appInitStateLivedata: LiveData<Boolean> = _appInitStateLivedata

    private val _propertyLogoUrlLiveData: MutableLiveData<String> = MutableLiveData()
    val propertyLogoUrlLiveData: LiveData<String> = _propertyLogoUrlLiveData

    fun initializeApp() {
        viewModelScope.launch {
            try {
                // for now clearing the cache when app starts
                cacheStore.clearAllCache()

                val isInitialized = initializeAppUseCase()
                themeManager.initialize(getThemingOptionsUseCase(), getHomeTemplateTypeUseCase())
                _propertyLogoUrlLiveData.value = getPropertyLogoUrlUseCase()
                preloadEPGData()
                _appInitStateLivedata.value = isInitialized
            } catch (ex: Exception) {
                Timber.e("Error while initializing the app $ex")
                ex.printStackTrace()
                onError(ex)
            }
        }
    }

    private suspend fun preloadEPGData() {
        try {
            getEPGUseCase()
        } catch (ex: Exception) {
            Timber.e("Error while preloading EPG data")
            Timber.e(ex)
        }
    }
}