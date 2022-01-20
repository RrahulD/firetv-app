package tv.accedo.dishonstream2.ui.main.home.dialog.programinfo

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import jp.wasabeef.blurry.Blurry
import kotlinx.datetime.Instant
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.ViewProgramInfoDialogBinding
import tv.accedo.dishonstream2.domain.model.supair.Channel
import tv.accedo.dishonstream2.domain.model.supair.Program
import tv.accedo.dishonstream2.extensions.copy
import tv.accedo.dishonstream2.extensions.getTimeString
import tv.accedo.dishonstream2.extensions.show
import tv.accedo.dishonstream2.ui.base.BaseFullScreenDialogFragment
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel
import tv.accedo.dishonstream2.ui.theme.ThemeManager

class ProgramInfoDialogFragment : BaseFullScreenDialogFragment() {
    private val programInfoDialogViewModel: ProgramInfoDialogViewModel by viewModel()
    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()
    private val themeManager: ThemeManager by inject()

    var onWatchButtonClick: ((Channel, Program, String) -> Unit)? = null

    companion object {
        private const val KEY_CHANNEL = "channel"
        private const val KEY_PROGRAM = "program"

        fun newInstance(channel: Channel, program: Program): ProgramInfoDialogFragment =
            ProgramInfoDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_CHANNEL, channel)
                    putParcelable(KEY_PROGRAM, program)
                }
            }
    }

    private lateinit var binding: ViewProgramInfoDialogBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ViewProgramInfoDialogBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val channel = arguments?.getParcelable<Channel>(KEY_CHANNEL)!!
        val program = arguments?.getParcelable<Program>(KEY_PROGRAM)!!

        programInfoDialogViewModel.programDetailsLiveData.observe(viewLifecycleOwner) { programInfo ->
            with(binding) {
                tvProgramDescription.text = programInfo?.description ?: ""
                binding.tvProgramRating.text = String.format("• %s", programInfo?.rating ?: "NA")

                Glide.with(requireContext())
                    .load(programInfo?.richMediaImageInfo?.imageURL)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?, model: Any?, target: Target<Drawable>?,
                            dataSource: DataSource?, isFirstResource: Boolean
                        ): Boolean {
                            context?.let { Blurry.with(it).from(resource?.toBitmap()).into(binding.ivProgramImgBlur) }
                            return false
                        }
                    })
                    .error(R.drawable.dish_image_not_found)
                    .into(binding.ivProgramImg)
            }
        }

        sharedAppViewModel.timeFormatLiveData.observe(viewLifecycleOwner) {
            val startTime = Instant.fromEpochMilliseconds(program.startTime).getTimeString(it)
            val endTime = Instant.fromEpochMilliseconds(program.endTime).getTimeString(it)
            binding.tvProgramSchedule.text = String.format("%s - %s", startTime, endTime)
        }

        sharedAppViewModel.largeFontEnabledLiveData.observe(viewLifecycleOwner, { enabled ->
            setZoomed(enabled)
        })

        with(binding) {
            binding.tvProgramTitle.text = program.name

            if (program.isLive()) {
                btnWatch.show()
                btnWatch.setOnClickListener {
                    onWatchButtonClick?.invoke(channel, program, binding.tvProgramRating.text.toString().replace("• ",""))
                }
            }
        }

        themeManager.getPrimaryButtonBackgroundDrawable()?.let {
            binding.btnWatch.background = it
            binding.btnRecord.background = it.copy()
        }

        programInfoDialogViewModel.loadProgramDetails(program.echoStarId)
    }

    private fun setZoomed(enabled: Boolean) {
        (binding.card.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentWidth =
            if (enabled) 0.57f else 0.5f
        binding.tvProgramTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 26f else 20f)
        binding.tvProgramSchedule.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            if (enabled) 20f else 14f
        )
        binding.tvProgramRating.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 20f else 14f)
        binding.tvProgramDescription.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            if (enabled) 20f else 14f
        )
        binding.tvProgramDescription.maxLines = if (enabled) 3 else 4
        binding.btnWatch.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 20f else 14f)
        binding.btnRecord.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 20f else 14f)
    }
}