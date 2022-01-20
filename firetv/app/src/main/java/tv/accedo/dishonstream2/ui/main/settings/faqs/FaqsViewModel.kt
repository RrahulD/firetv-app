package tv.accedo.dishonstream2.ui.main.settings.faqs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import timber.log.Timber
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.faq.GetFAQUseCase
import tv.accedo.dishonstream2.ui.base.BaseViewModel

class FaqsViewModel(
    private val getFAQUseCase: GetFAQUseCase
) : BaseViewModel() {

    private val _faqsLiveData = MutableLiveData<String>()
    val faqLiveData: LiveData<String> = _faqsLiveData

    init {
        getFAQ()
    }

    private fun getFAQ() {
        viewModelScope.launch {
            onLoading(true)
            try {
                val faq = getFAQUseCase()
                onLoading(false)
                _faqsLiveData.value = faq
            } catch (ex: Exception) {
                Timber.e(ex)
                onLoading(false)
                onError(ex)
            }
        }
    }
}