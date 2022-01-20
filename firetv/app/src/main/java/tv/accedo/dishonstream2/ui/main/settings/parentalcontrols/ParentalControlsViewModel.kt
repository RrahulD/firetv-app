package tv.accedo.dishonstream2.ui.main.settings.parentalcontrols

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import timber.log.Timber
import kotlinx.coroutines.launch
import tv.accedo.dishonstream2.domain.usecase.settings.parentalcontrols.GetPermittedClassificationsUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.parentalcontrols.SetPermittedClassificationsUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.parentalcontrols.createpin.GetParentalControlPinUseCase
import tv.accedo.dishonstream2.ui.base.BaseViewModel

class ParentalControlsViewModel(
    private val getParentalControlPinUseCase: GetParentalControlPinUseCase,
    private val setPermittedClassificationsUseCase: SetPermittedClassificationsUseCase,
    private val getPermittedClassificationsUseCase: GetPermittedClassificationsUseCase
) : BaseViewModel() {
    private val _parentalPinLiveData: MutableLiveData<String> = MutableLiveData()
    val parentalPinLiveData:LiveData<String> = _parentalPinLiveData

    private val _permittedClassificationsLiveData: MutableLiveData<List<String>> = MutableLiveData()
    val permittedClassificationsLiveData:LiveData<List<String>> = _permittedClassificationsLiveData

    init {
        getParentalControlPin()
    }

    fun getParentalControlPin() {
        viewModelScope.launch {
            try {
                getParentalControlPinUseCase()?.let {
                    _parentalPinLiveData.value = it.toString()
                }
            } catch (ex: Exception) {

            }
        }
    }

    /* setPermittedClassifications
    * to save the selected permitted classifications values into data storage
    * */
    fun setPermittedClassifications(value: LinkedHashSet<String>) {
        viewModelScope.launch {
            try {
                setPermittedClassificationsUseCase(value)
            } catch (ex: Exception) {
                Timber.tag(TAG).e("Exception in validateParentalPin() : $ex")
            }
        }

    }

    fun getPermittedClassifications() {
        viewModelScope.launch {
            try {
                getPermittedClassificationsUseCase()?.let {
                    _permittedClassificationsLiveData.value = it.toList()
                }
            } catch (ex: Exception) {
            }
        }
    }

    fun setDefaultPermittedClassifications() {
        val permittedClassifications = LinkedHashSet<String>()
        permittedClassifications.add(ParentalControlsFragment.KEY_ALL_AGES_SELECTED)
        permittedClassifications.add(ParentalControlsFragment.KEY_AGES26_SELECTED)
        permittedClassifications.add(ParentalControlsFragment.KEY_AGES7ANDUP_SELECTED)
        permittedClassifications.add(ParentalControlsFragment.KEY_AGES7ANDUP_FANTASY_SELECTED)
        permittedClassifications.add(ParentalControlsFragment.KEY_PARENTAL_GUIDANCE_SELECTED)
        permittedClassifications.add(ParentalControlsFragment.KEY_PARENTS_CAUTIONED)
        permittedClassifications.add(ParentalControlsFragment.KEY_UNSUITABLE_UNDER14)
        permittedClassifications.add(ParentalControlsFragment.KEY_UNSUITABLE_UNDER17)
        permittedClassifications.add(ParentalControlsFragment.KEY_RESTRICTED)
        permittedClassifications.add(ParentalControlsFragment.KEY_ADULTS_ONLY_SELECTED)
        permittedClassifications.add(ParentalControlsFragment.KEY_UNRATED_CONTENT_SELECTED)
        setPermittedClassifications(permittedClassifications)
    }

    companion object {
        const val TAG = "ParentalControl :: "
    }
}