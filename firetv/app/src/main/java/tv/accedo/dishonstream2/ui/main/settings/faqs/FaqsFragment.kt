package tv.accedo.dishonstream2.ui.main.settings.faqs

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import tv.accedo.dishonstream2.databinding.FaqsFragmentBinding
import tv.accedo.dishonstream2.ui.main.settings.base.BaseSubSettingFragment
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel

class FaqsFragment : BaseSubSettingFragment() {

    private val faqViewModel: FaqsViewModel by viewModel()
    private lateinit var binding: FaqsFragmentBinding
    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FaqsFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        faqViewModel.faqLiveData.observe(viewLifecycleOwner) { faqContent ->
            binding.faqContent.text = HtmlCompat.fromHtml(faqContent, HtmlCompat.FROM_HTML_MODE_LEGACY)
            binding.scrollView.requestFocus()
        }

        sharedAppViewModel.largeFontEnabledLiveData.observe(viewLifecycleOwner) { enabled ->
            setZoom(enabled)
        }
    }

    private fun setZoom(enabled: Boolean) {
        binding.header.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            if (enabled) 26f else 20f
        )
        binding.faqContent.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            if (enabled) 19f else 14f
        )
    }

    companion object {
        fun newInstance() = FaqsFragment()
    }
}