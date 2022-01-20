package tv.accedo.dishonstream2.ui.main.settings.parentalcontrols

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.ParentalControlsFragmentBinding
import tv.accedo.dishonstream2.extensions.copy
import tv.accedo.dishonstream2.extensions.hide
import tv.accedo.dishonstream2.extensions.show
import tv.accedo.dishonstream2.ui.main.search.ParentalPinChallenge
import tv.accedo.dishonstream2.ui.main.settings.base.BaseSubSettingFragment
import tv.accedo.dishonstream2.ui.main.settings.parentalcontrols.permittedclassifications.PermittedClassificationsFragment
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel

class ParentalControlsFragment : BaseSubSettingFragment() {
    private lateinit var binding: ParentalControlsFragmentBinding
    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()
    private val parentalControlsViewModel: ParentalControlsViewModel by viewModel()
    private var parentalControlPin: String = ""

    companion object {
        fun newInstance() = ParentalControlsFragment()
        const val KEY_ALL_AGES_SELECTED = "G"
        const val KEY_AGES26_SELECTED = "Y"
        const val KEY_AGES7ANDUP_SELECTED = "Y7"
        const val KEY_AGES7ANDUP_FANTASY_SELECTED = "Y7 FV"
        const val KEY_PARENTAL_GUIDANCE_SELECTED = "PG"
        const val KEY_PARENTS_CAUTIONED = "PG-13"
        const val KEY_UNSUITABLE_UNDER14 = "14"
        const val KEY_UNSUITABLE_UNDER17 = "MA"
        const val KEY_RESTRICTED = "R"
        const val KEY_ADULTS_ONLY_SELECTED = "NC-17"
        const val KEY_UNRATED_CONTENT_SELECTED = "UNRATED"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ParentalControlsFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentalControlsViewModel.getParentalControlPin()
        parentalControlsViewModel.parentalPinLiveData.observe(viewLifecycleOwner) { parentalPin ->
            Timber.i("parental control pin retrieved: %s", parentalPin.toString())
            parentalControlPin = parentalPin
            if (parentalControlPin.isNotEmpty()) {
                binding.setupPinSection.hide()
                binding.accessParentalControlSection.show()
            } else {
                binding.setupPinSection.show()
                binding.accessParentalControlSection.hide()
            }
        }

        binding.setupPinSection.setOnClickListener {
            setLastFocussedViewId(it.id)
            var parentalPinDialog =
                ParentalPinDialog(getString(R.string.enter_your_pin), getString(R.string.enter_your_pin_description), false)
            parentalPinDialog.pinEnteredCallback = { pin ->
                parentalControlPin = pin
                binding.setupPinSection.hide()
                binding.accessParentalControlSection.show()
                parentalControlsViewModel.setDefaultPermittedClassifications()
            }
            parentalPinDialog.show(requireActivity().supportFragmentManager)
        }

        binding.accessParentalControlSection.setOnClickListener {
            setLastFocussedViewId(it.id)
            val pinChallenge = ParentalPinChallenge("",
                parentalControlsViewModel, activity as FragmentActivity
            ) { context, challengeCallback -> }.setOnPassedListener {
                replaceFragment(PermittedClassificationsFragment.newInstance())
            }
            pinChallenge.run()
        }


        sharedAppViewModel.largeFontEnabledLiveData.observe(viewLifecycleOwner, { enabled ->
            binding.parentalControlsTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
            binding.setupPinText.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
            binding.accessParentalControlText.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
            binding.forgotPinText.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
        })

        themeManager.getSettingsSubOptionsBackgroundDrawable()?.let {
            binding.setupPinSection.background = it
            binding.accessParentalControlSection.background = it.copy()
            binding.forgotPinSection.background = it.copy()
        }
    }

}