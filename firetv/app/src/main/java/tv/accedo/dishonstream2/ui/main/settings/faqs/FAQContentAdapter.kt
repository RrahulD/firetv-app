package tv.accedo.dishonstream2.ui.main.settings.faqs

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tv.accedo.dishonstream2.databinding.FaqItemLayoutBinding
import tv.accedo.dishonstream2.domain.model.settings.FAQContent
import tv.accedo.dishonstream2.ui.theme.ThemeManager

@OptIn(KoinApiExtension::class)
class FAQContentAdapter(
    private val items: List<FAQContent>,
    private val isZoomEnabled: Boolean,
    private val lastFocusPos: Int?
) : RecyclerView.Adapter<FAQContentAdapter.FAQContentViewHolder>(), KoinComponent {

    private val themeManager: ThemeManager by inject()
    var onFAQContentClick: ((FAQContent, Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQContentAdapter.FAQContentViewHolder {
        return FAQContentViewHolder(
            FaqItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: FAQContentAdapter.FAQContentViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class FAQContentViewHolder(
        private val binding: FaqItemLayoutBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(faq: FAQContent, position: Int) {
            if (position == lastFocusPos) {
                binding.root.post {
                    binding.root.requestFocus()
                }
            }
            binding.header.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (isZoomEnabled) 18f else 14f)
            binding.header.text = faq.question.uppercase()
            themeManager.getSettingsSubOptionsBackgroundDrawable()?.let { binding.root.background = it }
            binding.root.setOnClickListener { onFAQContentClick?.invoke(faq, position) }
        }
    }
}