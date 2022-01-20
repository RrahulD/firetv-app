package tv.accedo.dishonstream2.ui.main.settings.parentalcontrols.permittedclassifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import timber.log.Timber
import tv.accedo.dishonstream2.domain.usecase.settings.parentalcontrols.GetPermittedClassificationsUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.parentalcontrols.SetPermittedClassificationsUseCase
import tv.accedo.dishonstream2.ui.base.BaseViewModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashSet
import kotlin.collections.Set as Set

class PermittedClassificationsViewModel(
    private val getPermittedClassificationsUseCase: GetPermittedClassificationsUseCase,
    private val setPermittedClassificationsUseCase: SetPermittedClassificationsUseCase
) : BaseViewModel() {
    private val permittedClassificationsMutableLiveData: MutableLiveData<List<String>> = MutableLiveData()
    val permittedClassificationsLiveData: LiveData<List<String>> = permittedClassificationsMutableLiveData

    init {
        getPermittedClassifications()
    }

    private fun getPermittedClassifications() {
        viewModelScope.launch {
            try {
                getPermittedClassificationsUseCase()?.let {
                    permittedClassificationsMutableLiveData.value = it.toList()
                    Timber.tag(TAG).i("set permitted classifications : %s", it.toList().toString())
                }
            } catch (ex: Exception) {
                Timber.tag(TAG).e("Exception in getPermittedClassifications() : $ex")
            }
        }
    }

    /* setPermittedClassifications
    * to save the selected permitted classifications values into data storage
    * */
    fun setPermittedClassifications(position: Int) {
        viewModelScope.launch {
            try {
                val recentPermittedClassifications = ArrayList<String>()
                getPermittedClassificationsUseCase()?.toList()?.let { recentPermittedClassifications.addAll(it) }
                val newPermittedClassifications = LinkedHashSet<String>()
                if (recentPermittedClassifications.size <= position) {
                    newPermittedClassifications.addAll(recentPermittedClassifications)
                    for (i in recentPermittedClassifications.size until position + 1) {
                        newPermittedClassifications.add(defaultPermittedClassifications()[i])
                    }
                    setPermittedClassificationsUseCase(newPermittedClassifications)
                    permittedClassificationsMutableLiveData.value = newPermittedClassifications.toList()
                } else {
                    if (recentPermittedClassifications.size > 1 && position > 0) {
                        for (i in recentPermittedClassifications.size - 1 downTo position) {
                            recentPermittedClassifications.removeAt(i)
                        }
                    } else {
                        for (i in recentPermittedClassifications.size - 1 downTo position + 1) {
                            recentPermittedClassifications.removeAt(i)
                        }
                    }
                    if (recentPermittedClassifications.size > 1) {
                        setPermittedClassificationsUseCase(recentPermittedClassifications.toSet() as LinkedHashSet<String>)
                    } else {
                        newPermittedClassifications.add(recentPermittedClassifications[0])
                        setPermittedClassificationsUseCase(newPermittedClassifications)
                    }
                    permittedClassificationsMutableLiveData.value = recentPermittedClassifications.toList()
                }
            } catch (ex: Exception) {
                Timber.tag(TAG).e(ex)
            }
        }
    }

    private fun defaultPermittedClassifications(): ArrayList<String> {
        val permittedClassifications = ArrayList<String>()
        permittedClassifications.add(PermittedClassificationsFragment.KEY_ALL_AGES_SELECTED)
        permittedClassifications.add(PermittedClassificationsFragment.KEY_AGES26_SELECTED)
        permittedClassifications.add(PermittedClassificationsFragment.KEY_AGES7ANDUP_SELECTED)
        permittedClassifications.add(PermittedClassificationsFragment.KEY_AGES7ANDUP_FANTASY_SELECTED)
        permittedClassifications.add(PermittedClassificationsFragment.KEY_PARENTAL_GUIDANCE_SELECTED)
        permittedClassifications.add(PermittedClassificationsFragment.KEY_PARENTS_CAUTIONED)
        permittedClassifications.add(PermittedClassificationsFragment.KEY_UNSUITABLE_UNDER14)
        permittedClassifications.add(PermittedClassificationsFragment.KEY_UNSUITABLE_UNDER17)
        permittedClassifications.add(PermittedClassificationsFragment.KEY_RESTRICTED)
        permittedClassifications.add(PermittedClassificationsFragment.KEY_ADULTS_ONLY_SELECTED)
        permittedClassifications.add(PermittedClassificationsFragment.KEY_UNRATED_CONTENT_SELECTED)
        return permittedClassifications
    }

    companion object {
        const val TAG = "PermittedClassificationFragment :: "
    }
}