package tv.accedo.dishonstream2.domain.usecase.settings.parentalcontrols.createpin

import android.util.Log
import timber.log.Timber
import tv.accedo.dishonstream2.domain.storage.KeyValueStore


class GetParentalPinValidationUseCase(
    private val keyValueStore: KeyValueStore
) {
    suspend operator fun invoke(enteredPin: String, reenteredPin: String): Boolean{
        Timber.tag(GetParentalPinValidationUseCase.TAG).i("parental control pin: %s", enteredPin.toString() + "_"+reenteredPin.toString())
      return enteredPin.equals(reenteredPin)
    }
    companion object {
        const val TAG = "GetParentalPinValidationUseCase :: "
    }
}