package tv.accedo.dishonstream2.ui.main.settings.faqs

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tv.accedo.dishonstream2.databinding.FaqSectionItemLayoutBinding
import tv.accedo.dishonstream2.domain.model.settings.FAQContent
import tv.accedo.dishonstream2.domain.model.settings.FAQSection
import tv.accedo.dishonstream2.ui.theme.ThemeManager

@OptIn(KoinApiExtension::class)
class FAQSectionAdapter(
    private val items: List<FAQSection>,
    private val isZoomEnabled: Boolean,
    private val lastFocussedPos: Pair<Int, Int>
) : RecyclerView.Adapter<FAQSectionAdapter.FQASectionViewHolder>(), KoinComponent {

    private val themeManager: ThemeManager by inject()
    var onFAQItemClick: ((String, FAQContent, Pair<Int, Int>) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FAQSectionAdapter.FQASectionViewHolder {
        return FQASectionViewHolder(
            FaqSectionItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: FAQSectionAdapter.FQASectionViewHolder, position: Int) {
        holder.onBind(items[position], position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class FQASectionViewHolder(
        private val binding: FaqSectionItemLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(faqSection: FAQSection, position: Int) {
            binding.section.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (isZoomEnabled) 18f else 14f)
            binding.section.text = faqSection.header.uppercase()
            binding.faqs.adapter = FAQContentAdapter(
                faqSection.faqs,
                isZoomEnabled,
                if (position == lastFocussedPos.first) lastFocussedPos.second else null
            ).apply {
                onFAQContentClick = { faqContent, lastClickedPos ->
                    onFAQItemClick?.invoke(faqSection.header, faqContent, Pair(position, lastClickedPos))
                }
            }
        }
    }

}