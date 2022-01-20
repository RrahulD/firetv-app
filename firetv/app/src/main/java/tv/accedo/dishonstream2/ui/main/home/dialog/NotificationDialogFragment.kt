package tv.accedo.dishonstream2.ui.main.home.dialog

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.ViewNotificationDialogBinding
import tv.accedo.dishonstream2.ui.base.BaseFullScreenDialogFragment
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel
import tv.accedo.dishonstream2.ui.theme.ThemeManager

class NotificationDialogFragment : BaseFullScreenDialogFragment() {

    companion object {
        private const val KEY_TITLE = "title"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_ICON_URL = "iconUrl"

        fun newInstance(title: String, description: String, iconUrl: String): NotificationDialogFragment =
            NotificationDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_TITLE, title)
                    putString(KEY_DESCRIPTION, description)
                    putString(KEY_ICON_URL, iconUrl)
                }
            }
    }

    private lateinit var binding: ViewNotificationDialogBinding
    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()
    private val themeManager: ThemeManager by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ViewNotificationDialogBinding.inflate(inflater)
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
            with(binding) {
                title.text = it.getString(KEY_TITLE)
                description.text = it.getString(KEY_DESCRIPTION)
                Glide.with(root.context)
                    .load(it.getString(KEY_ICON_URL))
                    .error(R.drawable.ic_baseline_notifications)
                    .into(ivIcon)
                btnAction.setOnClickListener { dismiss() }

                themeManager.getPrimaryButtonBackgroundDrawable()?.let { btnAction.background = it }
            }
        }
    }

    private fun setZoomed(enabled: Boolean) {
        (binding.card.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentWidth = if (enabled) 0.5f else 0.4f
        (binding.card.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentHeight = if (enabled) 0.7f else 0.6f

        binding.ivIcon.layoutParams.height = resources.getDimension(if (enabled) R.dimen._50dp else R.dimen._30dp).toInt()
        binding.ivIcon.layoutParams.width = resources.getDimension(if (enabled) R.dimen._50dp else R.dimen._30dp).toInt()

        binding.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 30f else 24f)
        binding.description.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 20f else 14f)
        binding.description.maxLines = if (enabled) 8 else 11

        (binding.btnAction.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentHeight =
            if (enabled) 0.5f else 0.4f
        binding.btnAction.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 20f else 14f)
    }
}