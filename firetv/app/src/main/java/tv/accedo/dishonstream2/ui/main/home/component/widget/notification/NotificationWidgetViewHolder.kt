package tv.accedo.dishonstream2.ui.main.home.component.widget.notification

import android.util.TypedValue
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.WidgetNotificationLayoutBinding
import tv.accedo.dishonstream2.domain.model.home.template.widget.NotificationWidget
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.WidgetViewHolder
import tv.accedo.dishonstream2.ui.main.home.dialog.NotificationDialogFragment

class NotificationWidgetViewHolder(
    private val binding: WidgetNotificationLayoutBinding,
    private val fragmentManager: FragmentManager
) : WidgetViewHolder(binding.root) {

    override val focusView = binding.container

    fun populate(widget: NotificationWidget) {
        with(binding) {
            notificationTitle.text = widget.title
            notificationMessage.text = widget.description
            Glide.with(root)
                .load(widget.icon.fileUrl)
                .error(R.drawable.ic_baseline_notifications)
                .into(notificationLogo)
            container.setOnClickListener {
                NotificationDialogFragment.newInstance(widget.title, widget.description, widget.icon.fileUrl)
                    .show(fragmentManager)
            }
        }
    }

    fun setZoom() {
        binding.notificationLogo.layoutParams.width = binding.root.resources.getDimension(R.dimen._30dp).toInt()
        binding.notificationLogo.layoutParams.height = binding.root.resources.getDimension(R.dimen._30dp).toInt()
        binding.notificationTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        binding.notificationMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        binding.notificationMessage.maxLines = 7
    }
}