package tv.accedo.dishonstream2.ui.main.home.dialog

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.ViewContentErrorDialogBinding
import tv.accedo.dishonstream2.ui.base.BaseFullScreenDialogFragment
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel
import tv.accedo.dishonstream2.ui.theme.ThemeManager

class ContentErrorDialogFragment : BaseFullScreenDialogFragment() {

    private val themeManager: ThemeManager by inject()
    var onActionButtonClick: (() -> Unit)? = null

    companion object {
        private const val KEY_TITLE = "title"
        private const val KEY_DESCRIPTION = "description"
        fun newInstance(title: String, description: String): ContentErrorDialogFragment =
            ContentErrorDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_TITLE, title)
                    putString(KEY_DESCRIPTION, description)
                }
            }
    }

    private lateinit var binding: ViewContentErrorDialogBinding
    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ViewContentErrorDialogBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedAppViewModel.largeFontEnabledLiveData.observe(viewLifecycleOwner, { enabled ->
            populateData()
            setZoomed(enabled)
        })
    }

    private fun populateData() {
        arguments?.let {
            binding.title.text = it.getString(KEY_TITLE)
            binding.description.text = it.getString(KEY_DESCRIPTION)
        }
        binding.btnAction.setOnClickListener {
            onActionButtonClick?.invoke()
            dismiss()
        }

        themeManager.getPrimaryButtonBackgroundDrawable()?.let { binding.btnAction.background = it }
    }

    private fun setZoomed(enabled: Boolean) {
        (binding.card.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentWidth = if (enabled) 0.5f else 0.4f
        (binding.card.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentHeight = if (enabled) 0.7f else 0.6f

        binding.ivIcon.layoutParams.height =
            resources.getDimension(if (enabled) R.dimen._65dp else R.dimen._60dp).toInt()
        binding.ivIcon.layoutParams.width =
            resources.getDimension(if (enabled) R.dimen._65dp else R.dimen._60dp).toInt()

        binding.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 27f else 22f)
        binding.description.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 19f else 14f)

        (binding.btnAction.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentHeight =
            if (enabled) 0.5f else 0.4f
        binding.btnAction.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 19f else 14f)
    }
}