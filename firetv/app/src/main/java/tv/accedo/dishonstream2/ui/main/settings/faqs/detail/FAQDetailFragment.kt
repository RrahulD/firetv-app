package tv.accedo.dishonstream2.ui.main.settings.faqs.detail

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import tv.accedo.dishonstream2.databinding.FaqDetailFragmentBinding
import tv.accedo.dishonstream2.ui.main.settings.base.BaseSubSettingFragment
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel

class FAQDetailFragment : BaseSubSettingFragment() {

    private lateinit var binding: FaqDetailFragmentBinding
    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FaqDetailFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.run {
            binding.question.text = getString(KEY_QUESTION)!!
            binding.section.text = getString(KEY_SECTION)!!.uppercase()
            binding.answer.text = getString(KEY_ANSWER)!!
            binding.scrollView.requestFocus()
        }
        sharedAppViewModel.largeFontEnabledLiveData.observe(viewLifecycleOwner, { enabled ->
            binding.question.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                if (enabled) 26f else 20f
            )
            binding.section.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                if (enabled) 17f else 14f
            )
            binding.answer.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                if (enabled) 19f else 14f
            )
        })
    }

    companion object {
        private const val KEY_QUESTION = "question"
        private const val KEY_ANSWER = "answer"
        private const val KEY_SECTION = "section"

        fun newInstance(section: String, question: String, answer: String) =
            FAQDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_SECTION, section)
                    putString(KEY_QUESTION, question)
                    putString(KEY_ANSWER, answer)
                }
            }
    }
}
