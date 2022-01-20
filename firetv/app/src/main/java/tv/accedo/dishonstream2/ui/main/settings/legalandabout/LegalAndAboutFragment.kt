package tv.accedo.dishonstream2.ui.main.settings.legalandabout

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.LegalAndAboutFragmentBinding
import tv.accedo.dishonstream2.extensions.copy
import tv.accedo.dishonstream2.ui.main.settings.base.BaseSubSettingFragment
import tv.accedo.dishonstream2.ui.main.settings.legalandabout.privacypolicy.PrivacyPolicyFragment
import tv.accedo.dishonstream2.ui.main.settings.legalandabout.termsofservice.TermsOfServiceFragment
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel

class LegalAndAboutFragment : BaseSubSettingFragment() {

    private lateinit var binding: LegalAndAboutFragmentBinding
    private val vm: LegalAndAboutViewModel by viewModel()
    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = LegalAndAboutFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedAppViewModel.largeFontEnabledLiveData.observe(viewLifecycleOwner, { enabled ->
            setZoom(enabled)
        })
        vm.appVersionLiveData.observe(viewLifecycleOwner, { appVersion ->
            binding.appVersion.text = appVersion
        })

        vm.deviceInformationLiveData.observe(viewLifecycleOwner, { deviceInformation ->
            binding.deviceInformation.text = deviceInformation
        })

        vm.contactLiveData.observe(viewLifecycleOwner, { contact ->
            binding.contact.text = contact
        })

        binding.termsOfServiceContainer.setOnClickListener {
            setLastFocussedViewId(it.id)
            replaceFragment(TermsOfServiceFragment.newInstance())
        }

        binding.privacyPolicyContainer.setOnClickListener {
            setLastFocussedViewId(it.id)
            replaceFragment(PrivacyPolicyFragment.newInstance())
        }

        themeManager.getSettingsSubOptionsBackgroundDrawable()?.let {
            with(binding) {
                termsOfServiceContainer.background = it
                privacyPolicyContainer.background = it.copy()
            }
        }
    }

    private fun setZoom(enabled: Boolean) {
        binding.legal.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
        binding.termsOfService.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
        binding.privacyPolicy.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
        binding.about.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            if (enabled) 20f else 15f
        )
        binding.appVersionHeader.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            if (enabled) 25f else 20f
        )
        binding.appVersion.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            if (enabled) 20f else 15f
        )
        binding.deviceInformationHeader.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            if (enabled) 25f else 20f
        )
        binding.deviceInformation.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            if (enabled) 20f else 15f
        )
        binding.contactHeader.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            if (enabled) 25f else 20f
        )
        binding.contact.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            if (enabled) 20f else 15f
        )

        if (enabled) {
            binding.privacyPolicyContainer.nextFocusDownId = R.id.contact
            binding.contact.isFocusable = true
        } else {
            binding.privacyPolicyContainer.nextFocusDownId = R.id.privacy_policy_container
            binding.contact.isFocusable = false
        }
    }

    companion object {
        fun newInstance() = LegalAndAboutFragment()
    }

}