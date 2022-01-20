package tv.accedo.dishonstream2.ui.main.search

import androidx.fragment.app.FragmentActivity
import hu.accedo.commons.challenges.Challenge
import hu.accedo.commons.challenges.ChallengeBuilder
import timber.log.Timber
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.ui.main.settings.parentalcontrols.ParentalControlsViewModel
import tv.accedo.dishonstream2.ui.main.settings.parentalcontrols.ParentalPinDialog
import tv.accedo.dishonstream2.ui.main.settings.parentalcontrols.permittedclassifications.PermittedClassificationsViewModel

class ParentalPinChallenge(
    val rating: String?,
    private val parentalControlViewModel: ParentalControlsViewModel,
    val context: FragmentActivity,
    private val challenge: Challenge
) :
    ChallengeBuilder(context) {

    var onPassedListener: OnPassedListener? = null
    var onFailedListener: OnFailedListener? = null

    override fun setOnPassedListener(onPassedListener: OnPassedListener?): ChallengeBuilder {
        this.onPassedListener = onPassedListener
        return this
    }

    override fun setOnFailedListener(onFailedListener: OnFailedListener?): ChallengeBuilder {
        this.onFailedListener = onFailedListener
        return this
    }

    override fun run() {
        super.run()
        try {
            var parentalPin = ""
            parentalControlViewModel.getParentalControlPin()
            parentalControlViewModel.parentalPinLiveData.observe(context) { parentalControlPin ->
                parentalPin = parentalControlPin
                parentalControlViewModel.parentalPinLiveData.removeObservers(context)
            }
            if (parentalPin.isNotEmpty()) {
                parentalControlViewModel.getPermittedClassifications()
                parentalControlViewModel.permittedClassificationsLiveData.observe(context) { permittedClassifications ->
                    if (rating?.isEmpty() == true || !permittedClassifications.contains(rating)) {
                        val parentalPinDialog = ParentalPinDialog.newInstance()
                        parentalPinDialog.pinEnteredCallback = { pin ->
                            if (pin.equals(parentalPin)) {
                                onPassedListener?.challengesPassed()
                                parentalPinDialog.dismiss()
                            } else {
                                parentalPinDialog.showError(context.getString(R.string.incorrect_pin))
                            }
                        }
                        parentalPinDialog.show(context.supportFragmentManager)
                    } else {
                        onPassedListener?.challengesPassed()
                    }
                    parentalControlViewModel.permittedClassificationsLiveData.removeObservers(context)
                }
            } else {
                onPassedListener?.challengesPassed()
            }
        } catch (ex: Exception) {
            Timber.tag(PermittedClassificationsViewModel.TAG).e("Exception in getPermittedClassifications() : $ex")
        }
    }
}