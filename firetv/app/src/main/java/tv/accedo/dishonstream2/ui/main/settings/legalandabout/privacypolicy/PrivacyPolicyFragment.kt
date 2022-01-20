package tv.accedo.dishonstream2.ui.main.settings.legalandabout.privacypolicy

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import tv.accedo.dishonstream2.databinding.PrivacyPolicyFragmentBinding
import tv.accedo.dishonstream2.ui.main.settings.base.BaseSubSettingFragment
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel

class PrivacyPolicyFragment : BaseSubSettingFragment() {

    private lateinit var binding: PrivacyPolicyFragmentBinding
    private val privacyPolicyViewModel: PrivacyPolicyViewModel by viewModel()
    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = PrivacyPolicyFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        privacyPolicyViewModel.privacyPolicyLiveData.observe(viewLifecycleOwner, { privacyPolicy ->
            binding.paragraph.text = privacyPolicy
            binding.scrollView.requestFocus()
        })
        sharedAppViewModel.largeFontEnabledLiveData.observe(viewLifecycleOwner, { enabled ->
            setZoom(enabled)
        })
    }

    private fun setZoom(enabled: Boolean) {
        binding.header.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            if (enabled) 26f else 20f
        )
        binding.paragraph.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            if (enabled) 19f else 14f
        )
    }

    companion object {
        fun newInstance() = PrivacyPolicyFragment()
    }
}