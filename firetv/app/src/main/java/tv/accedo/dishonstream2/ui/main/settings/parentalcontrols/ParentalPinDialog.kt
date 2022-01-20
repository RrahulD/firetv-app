package tv.accedo.dishonstream2.ui.main.settings.parentalcontrols

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.EnterPinDialogBinding
import tv.accedo.dishonstream2.ui.base.BaseFullScreenDialogFragment
import tv.accedo.dishonstream2.ui.main.home.dialog.ParentalControlCustomDialogFragment
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel


class ParentalPinDialog(val title: String, val description: String, val isChallenge: Boolean) : BaseFullScreenDialogFragment() {
    private lateinit var binding: EnterPinDialogBinding
    var pinEnteredCallback: ((String) -> Unit)? = null
    var isReenterPinDialog = false
    private lateinit var enteredPin: String
    private var inputConnection: InputConnection? = null
    private val parentalPinValidationViewModel: ParentalPinValidationViewModel by viewModel()
    private val parentalControlCreatePinViewModel: ParentalControlCreatePinViewModel by viewModel()
    private var isPinSetupDisplayed: Boolean = false
    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()
    private lateinit  var enteredPin1: String
    private lateinit  var enteredPin2: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = EnterPinDialogBinding.inflate(inflater);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.enterPinText.setText(title)
        binding.pinDescriptionText.setText(description)
        inputConnection = binding.pinEditText.onCreateInputConnection(EditorInfo())
        binding.numberKeyboardLayout.inputConnection = inputConnection
        binding.numberKeyboardLayout.requestFocus()
        hideSoftKeyboardForEditText()



        binding.pinEditText.addTextChangedListener{
            enteredPin = it?.toString().toString()
            val chars: CharArray = enteredPin.toCharArray()
            binding.incorrectPinText.visibility=View.INVISIBLE
            setEditTextBackground(chars)
            clearPin()
             if (chars.size > 0) {

                if (KEY_PINSIZE_0 < chars.size) {
                    binding.pin1EditText.setText(chars[KEY_PINSIZE_0].toString())
                }
                if (KEY_PINSIZE_1 < chars.size) {
                    binding.pin2EditText.setText(chars[KEY_PINSIZE_1].toString())
                }
                if (KEY_PINSIZE_2 < chars.size) {
                    binding.pin3EditText.setText(chars[KEY_PINSIZE_2].toString())
                }
                if (KEY_PINSIZE_3 < chars.size) {
                    binding.pin4EditText.setText(chars[KEY_PINSIZE_3].toString())
                    lifecycleScope.launch {
                        delay(1000)
                        if (isChallenge)
                            pinEnteredCallback?.invoke(getPinFromEditText())
                        else
                            decideNavigation()
                    }
                }

            }

        }

        sharedAppViewModel.largeFontEnabledLiveData.observe(viewLifecycleOwner, { enabled ->
            setZoomed(enabled)
        })
    }

    fun decideNavigation() {
        if (binding.pin1EditText.getText().toString().trim().isEmpty() || binding.pin2EditText.getText().trim().toString()
                .trim().isEmpty()
            || binding.pin3EditText.getText().trim().toString().trim().isEmpty() || binding.pin4EditText.getText().trim()
                .toString().trim().isEmpty()
        ) {
            return
        }

        if (isReenterPinDialog) {
            enteredPin2 = getPinFromEditText()
            parentalPinValidationViewModel.validateParentalPin(enteredPin1, enteredPin2)
            parentalPinValidationViewModel.parentalPinValidationLiveData.observe(viewLifecycleOwner) {
                if (it) {
                    if (!isPinSetupDisplayed) {
                        parentalControlCreatePinViewModel.setParentalControlPin(
                            binding.pin1EditText.getText().toString()
                                + binding.pin2EditText.getText().toString() + binding.pin3EditText.getText().toString() +
                                binding.pin4EditText.getText().toString()
                        )
                        pinEnteredCallback?.invoke(getPinFromEditText())
                        showPinSetupPopup()
                        isPinSetupDisplayed = true
                    }
                } else {
                    binding.pinEditText.text.clear()
                    binding.numberKeyboardLayout.getFocasable().requestFocus()
                    binding.incorrectPinText.visibility = View.VISIBLE
                    binding.incorrectPinText.setText(R.string.reenter_incorrect_pin)
                }
            }

        } else {
            enteredPin1 = getPinFromEditText()
            binding.enterPinText.setText(getString(R.string.re_enter_your_pin))
            binding.pinDescriptionText.setText(getString(R.string.enter_your_pin_description))
            binding.numberKeyboardLayout.getFocasable().requestFocus()
            isReenterPinDialog = true
            clearPin()
            binding.pinEditText.getText().clear()
        }
    }

    fun getPinFromEditText(): String {
        try {
            return binding.pin1EditText.getText().toString() + binding.pin2EditText.getText()
                .toString() + binding.pin3EditText.getText().toString() +
                binding.pin4EditText.getText().toString()
        } catch (ex: Exception) {
            Timber.tag(TAG).e("Exception in get parental pin dialog : $ex")
        }
        return ""
    }

    fun hideSoftKeyboardForEditText() {
        binding.pin1EditText.showSoftInputOnFocus = false
        binding.pin2EditText.showSoftInputOnFocus = false
        binding.pin3EditText.showSoftInputOnFocus = false
        binding.pin4EditText.showSoftInputOnFocus = false

        binding.pin1EditText.isCursorVisible = false
        binding.pin2EditText.isCursorVisible = false
        binding.pin3EditText.isCursorVisible = false
        binding.pin4EditText.isCursorVisible = false

    }

    fun setEditTextBackground(chars: CharArray) {
        binding.pin3EditText.setBackgroundResource(R.drawable.rounded_background)
        binding.pin4EditText.setBackgroundResource(R.drawable.rounded_background)
        binding.pin1EditText.setBackgroundResource(R.drawable.rounded_background)
        binding.pin2EditText.setBackgroundResource(R.drawable.rounded_background)

        when (chars.size) {
            1 -> binding.pin1EditText.setBackgroundResource(R.drawable.rounded_background_selected)
            2 -> binding.pin2EditText.setBackgroundResource(R.drawable.rounded_background_selected)
            3 -> binding.pin3EditText.setBackgroundResource(R.drawable.rounded_background_selected)
            4 -> binding.pin4EditText.setBackgroundResource(R.drawable.rounded_background_selected)
        }
    }

    fun clearPin() {
        binding.pin1EditText.getText().clear()
        binding.pin2EditText.getText().clear()
        binding.pin3EditText.getText().clear()
        binding.pin4EditText.getText().clear()
    }

    fun showPinSetupPopup() {

        parentalControlCreatePinViewModel.createPinLiveData.observe(viewLifecycleOwner) {
            this.dismiss()
            var parentalControlCustomDialogFragment = ParentalControlCustomDialogFragment.newInstance(
                R.drawable.ic_tick,
                getString(R.string.content_pin_setup),
                getString(R.string.content_pin_setup_message)
            )
            parentalControlCustomDialogFragment.show(requireActivity().supportFragmentManager)
        }
    }

    companion object {
        private const val KEY_PINSIZE_0 = 0
        private const val KEY_PINSIZE_1 = 1
        private const val KEY_PINSIZE_2 = 2
        private const val KEY_PINSIZE_3 = 3
        private const val KEY_ENTERED_PIN = "KEY_ENTERED_PIN"
        private const val KEY_ENTER_YOUR_PIN = "Enter Your PIN Number"
        private const val KEY_YOUR_PIN_DESCRIPTION = "Enter your PIN number to make changes in parental control"
        private const val TAG = "ParentalPinDialog :: "
        fun newInstance() = ParentalPinDialog(KEY_ENTER_YOUR_PIN, KEY_YOUR_PIN_DESCRIPTION, true)
    }


    private fun setZoomed(enabled: Boolean) {
        (binding.card.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentWidth = if (enabled) 0.7f else 0.6f
        (binding.card.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentHeight = if (enabled) 0.7f else 0.6f
        binding.enterPinText.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 32f else 28f)
        binding.pinDescriptionText.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 20f else 16f)
        (binding.numberKeyboardLayout.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentHeight =
            if (enabled) 0.7f else 0.6f
        binding.numberKeyboardLayout.setZoomed(enabled)
    }
    fun showError(errorText: String){
        binding.pinEditText.text.clear()
        binding.numberKeyboardLayout.getFocasable().requestFocus()
        binding.incorrectPinText.visibility=View.VISIBLE
        binding.incorrectPinText.setText(errorText)
    }
}