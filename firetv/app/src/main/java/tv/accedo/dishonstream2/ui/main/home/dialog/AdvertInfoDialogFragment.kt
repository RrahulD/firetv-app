package tv.accedo.dishonstream2.ui.main.home.dialog

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.parcelize.Parcelize
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.ViewAdvertDialogBinding
import tv.accedo.dishonstream2.extensions.hide
import tv.accedo.dishonstream2.extensions.show
import tv.accedo.dishonstream2.ui.base.BaseFullScreenDialogFragment
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel

class AdvertInfoDialogFragment : BaseFullScreenDialogFragment() {

    @Parcelize
    data class AdvertDialogInfo(
        val imgUrl: String,
        val title: String,
        val subTitle: String,
        val description: String,
        val link: String
    ) : Parcelable

    companion object {
        private const val KEY_ADVERT_INFO = "advert_info"

        fun newInstance(advertInfo: AdvertDialogInfo): AdvertInfoDialogFragment =
            AdvertInfoDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_ADVERT_INFO, advertInfo)
                }
            }
    }

    private lateinit var binding: ViewAdvertDialogBinding
    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ViewAdvertDialogBinding.inflate(inflater)
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
        arguments?.getParcelable<AdvertDialogInfo>(KEY_ADVERT_INFO)?.let { advertInfo ->
            with(binding) {
                loadAdvertPoster(advertInfo.imgUrl)
                adTitle.text = advertInfo.title
                adSubtitle.text = advertInfo.subTitle
                adDescription.text = advertInfo.description
            }
        }
    }

    private fun loadAdvertPoster(imageUrl: String) {
        binding.progressBar.show()
        Glide.with(requireContext())
            .load(imageUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.progressBar.hide()
                    binding.ivAdvertImg.setImageResource(R.drawable.dish_image_not_found)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.progressBar.hide()
                    binding.ivAdvertImg.setImageDrawable(resource)
                    return true
                }
            })
            .into(binding.ivAdvertImg)
    }

    private fun setZoomed(enabled: Boolean) {
        (binding.card.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentWidth =
            if (enabled) 0.57f else 0.5f
        binding.adTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 26f else 20f)
        binding.adSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 20f else 16f)
        binding.adDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 20f else 16f)
        binding.advertText.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 19f else 14f)
    }
}