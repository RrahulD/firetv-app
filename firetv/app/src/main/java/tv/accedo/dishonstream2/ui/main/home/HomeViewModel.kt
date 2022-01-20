package tv.accedo.dishonstream2.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import timber.log.Timber
import tv.accedo.dishonstream2.domain.model.home.template.hometemplate.HomeTemplate
import tv.accedo.dishonstream2.domain.usecase.home.GetHomeTemplateUseCase
import tv.accedo.dishonstream2.ui.base.BaseViewModel

class HomeViewModel(
    private val getHomeTemplateUseCase: GetHomeTemplateUseCase
) : BaseViewModel() {

    private val _homePageTemplate = MutableLiveData<HomeTemplate>()
    val homePageTemplate: LiveData<HomeTemplate> = _homePageTemplate

    init {
        getHomePageTemplate()
    }

    private fun getHomePageTemplate() {
        viewModelScope.launch {
            onLoading(true)
            try {
                val template = getHomeTemplateUseCase()
                onLoading(false)
                _homePageTemplate.value = template
            } catch (ex: Exception) {
                Timber.e(ex)
                onLoading(false)
                onError(RuntimeException("Something went wrong! We are unable to load the home screen, please try after some time."))
            }
        }
    }
}