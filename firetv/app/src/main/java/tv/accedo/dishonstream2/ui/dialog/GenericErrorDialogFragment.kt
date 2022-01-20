package tv.accedo.dishonstream2.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.android.ext.android.inject
import tv.accedo.dishonstream2.databinding.GenericErrorDialogFragmentBinding
import tv.accedo.dishonstream2.ui.base.BaseFullScreenDialogFragment
import tv.accedo.dishonstream2.ui.theme.ThemeManager

class GenericErrorDialogFragment : BaseFullScreenDialogFragment() {

    private val themeManager: ThemeManager by inject()

    companion object {
        private const val KEY_TITLE = "title"
        private const val KEY_MESSAGE = "message"

        fun newInstance(title: String, message: String): GenericErrorDialogFragment =
            GenericErrorDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_TITLE, title)
                    putString(KEY_MESSAGE, message)
                }
            }
    }

    private lateinit var binding: GenericErrorDialogFragmentBinding
    var onCtaClick: (() -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = GenericErrorDialogFragmentBinding.inflate(inflater)
        isCancelable = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            binding.title.text = it.getString(KEY_TITLE)
            binding.message.text = it.getString(KEY_MESSAGE)
        }
        themeManager.getPrimaryButtonBackgroundDrawable()?.let { binding.ctaButton.background = it }
        binding.ctaButton.setOnClickListener {
            onCtaClick?.invoke()
            dismiss()
        }
        binding.ctaButton.requestFocus()
    }
}