package tv.accedo.dishonstream2.ui.main.home.dialog

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.MoreInfoContentItemLayoutBinding
import tv.accedo.dishonstream2.databinding.MoreInfoDialogLayoutBinding
import tv.accedo.dishonstream2.domain.model.home.template.widget.MoreInfoWidget
import tv.accedo.dishonstream2.extensions.hide
import tv.accedo.dishonstream2.ui.base.BaseFullScreenDialogFragment
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel

class MoreInfoDialogFragment : BaseFullScreenDialogFragment() {

    companion object {
        private const val KEY_MORE_INFO = "more_info"

        fun newInstance(moreInfo: MoreInfoWidget): MoreInfoDialogFragment =
            MoreInfoDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_MORE_INFO, moreInfo)
                }
            }
    }

    private lateinit var binding: MoreInfoDialogLayoutBinding
    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()
    private var isZoomEnabled = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = MoreInfoDialogLayoutBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedAppViewModel.largeFontEnabledLiveData.observe(viewLifecycleOwner, { enabled ->
            isZoomEnabled = enabled
            populateData()
            setZoomed(enabled)
        })
    }

    private fun populateData() {
        arguments?.getParcelable<MoreInfoWidget>(KEY_MORE_INFO)?.let { moreInfo ->
            Glide.with(binding.root)
                .load(moreInfo.headerImage.fileUrl)
                .into(binding.headerImage)

            binding.title.text = moreInfo.title
            binding.subtitle.text = moreInfo.subTitle
            binding.description.text = moreInfo.contentTitle

            val layoutInflater = LayoutInflater.from(binding.root.context)
            moreInfo.contents.forEach { content ->
                val moreInfoLayout = MoreInfoContentItemLayoutBinding.inflate(layoutInflater, binding.container, false)
                with(moreInfoLayout) {
                    if (content.header.isNotBlank()) header.text = content.header else header.hide()

                    if (content.paragraph.isNotBlank()) paragraph.text = content.paragraph else paragraph.hide()

                    if (content.image.fileUrl.isNotBlank())
                        Glide.with(binding.root)
                            .load(content.image.fileUrl)
                            .into(image)
                    else image.hide()

                    header.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (isZoomEnabled) 20f else 14f)
                    paragraph.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (isZoomEnabled) 20f else 14f)
                    image.layoutParams.width =
                        root.resources.getDimension(if (isZoomEnabled) R.dimen._300dp else R.dimen._250dp).toInt()
                    image.layoutParams.height =
                        root.resources.getDimension(if (isZoomEnabled) R.dimen._200dp else R.dimen._150dp).toInt()
                    binding.container.addView(moreInfoLayout.root)
                }
            }
        }
    }

    private fun setZoomed(enabled: Boolean) {
        binding.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 26f else 20f)
        binding.subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 20f else 14f)
        binding.description.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 20f else 14f)
        (binding.card.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentWidth =
            if (enabled) 0.57f else 0.5f
    }
}