package tv.accedo.dishonstream2.ui.main.settings.parentalcontrols.permittedclassifications


import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.PermittedClassificationsFragmentBinding
import tv.accedo.dishonstream2.domain.usecase.settings.parentalcontrols.GetPermittedClassificationsUseCase
import tv.accedo.dishonstream2.extensions.copy
import tv.accedo.dishonstream2.extensions.hide
import tv.accedo.dishonstream2.extensions.show
import tv.accedo.dishonstream2.ui.main.home.dialog.ParentalControlCustomDialogFragment
import tv.accedo.dishonstream2.ui.main.settings.base.BaseSubSettingFragment
import tv.accedo.dishonstream2.ui.main.settings.parentalcontrols.ParentalControlsFragment

import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel
import tv.accedo.dishonstream2.ui.theme.ThemeManager

class PermittedClassificationsFragment : BaseSubSettingFragment() {

    private lateinit var binding: PermittedClassificationsFragmentBinding
    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()
    private val permittedClassificationsViewModel: PermittedClassificationsViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PermittedClassificationsFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.allAges.requestFocus()

        binding.allAges.setOnClickListener {
            setLastFocussedViewId(it.id)
            handlePermittedClassificationSelection(0)
            ParentalControlCustomDialogFragment.newInstance(
                R.drawable.ic_alert,
                getString(R.string.enable_one_classification_dialog_title),
                getString(R.string.enable_one_classification_dialog_text)
            ).show(requireActivity().supportFragmentManager)
        }

        binding.ages26.setOnClickListener {
            setLastFocussedViewId(it.id)
            handlePermittedClassificationSelection(1)
        }
        binding.ages7AndUp.setOnClickListener {
            setLastFocussedViewId(it.id)
            handlePermittedClassificationSelection(2)
        }

        binding.ages7AndUpFantasy.setOnClickListener {
            setLastFocussedViewId(it.id)
            handlePermittedClassificationSelection(3)
        }

        binding.parentalGuidanceRecommended.setOnClickListener {
            setLastFocussedViewId(it.id)
            handlePermittedClassificationSelection(4)
        }

        binding.parentsStronglyCautioned.setOnClickListener {
            setLastFocussedViewId(it.id)
            handlePermittedClassificationSelection(5)
        }

        binding.unsuitableUnder14.setOnClickListener {
            setLastFocussedViewId(it.id)
            handlePermittedClassificationSelection(6)
        }
        binding.unsuitableUnder17.setOnClickListener {
            setLastFocussedViewId(it.id)
            handlePermittedClassificationSelection(7)
        }
        binding.restricted.setOnClickListener {
            setLastFocussedViewId(it.id)
            handlePermittedClassificationSelection(8)
        }
        binding.adultsOnly.setOnClickListener {
            setLastFocussedViewId(it.id)
            handlePermittedClassificationSelection(9)
        }

        binding.unratedContent.setOnClickListener {
            setLastFocussedViewId(it.id)
            handlePermittedClassificationSelection(10)
        }
        binding.changesSaved.setOnClickListener {
            setLastFocussedViewId(it.id)
            replaceFragment(ParentalControlsFragment.newInstance())
        }

        sharedAppViewModel.largeFontEnabledLiveData.observe(viewLifecycleOwner, { enabled ->
            binding.youngKidsTitleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
            binding.allAgesText.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
            binding.ages26Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
            binding.kidsTitleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
            binding.ages7AndUpText.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
            binding.ages7AndUpFantasyText.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
            binding.teenagersTitleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
            binding.parentalGuidanceRecommendedText.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
            binding.parentsStronglyCautionedText.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
            binding.unsuitableUnder14Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
            binding.adultsTitleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
            binding.unsuitableUnder17Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
            binding.restrictedText.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
            binding.adultsOnlyText.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
            binding.unratedContentText.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
            binding.restoreDefaultSettingsText.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
            binding.resetAppSettingsText.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)
            binding.changesSavedText.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 18f else 14f)

            setZoomedImages(enabled)

        })
        permittedClassificationsViewModel.permittedClassificationsLiveData.observe(viewLifecycleOwner) { recentPermittedClassifications ->

            if (recentPermittedClassifications.contains(KEY_ALL_AGES_SELECTED))
                binding.allAgesSelected.show() else binding.allAgesSelected.hide()
            if (recentPermittedClassifications.contains(KEY_AGES26_SELECTED))
                binding.ages26Selected.show() else binding.ages26Selected.hide()
            if (recentPermittedClassifications.contains(KEY_AGES7ANDUP_SELECTED))
                binding.ages7AndUpSelected.show() else binding.ages7AndUpSelected.hide()
            if (recentPermittedClassifications.contains(KEY_AGES7ANDUP_FANTASY_SELECTED))
                binding.ages7AndUpFantasySelected.show() else binding.ages7AndUpFantasySelected.hide()
            if (recentPermittedClassifications.contains(KEY_PARENTAL_GUIDANCE_SELECTED))
                binding.parentalGuidanceSelected.show() else binding.parentalGuidanceSelected.hide()
            if (recentPermittedClassifications.contains(KEY_PARENTS_CAUTIONED))
                binding.parentsStronglyCautionedSelected.show() else binding.parentsStronglyCautionedSelected.hide()
            if (recentPermittedClassifications.contains(KEY_UNSUITABLE_UNDER14))
                binding.unsuitableUnder14Selected.show() else binding.unsuitableUnder14Selected.hide()
            if (recentPermittedClassifications.contains(KEY_UNSUITABLE_UNDER17))
                binding.unsuitableUnder17Selected.show() else binding.unsuitableUnder17Selected.hide()
            if (recentPermittedClassifications.contains(KEY_RESTRICTED))
                binding.restrictedSelected.show() else binding.restrictedSelected.hide()
            if (recentPermittedClassifications.contains(KEY_ADULTS_ONLY_SELECTED))
                binding.adultsOnlySelected.show() else binding.adultsOnlySelected.hide()
            if (recentPermittedClassifications.contains(KEY_UNRATED_CONTENT_SELECTED))
                binding.unratedContentSelected.show() else binding.unratedContentSelected.hide()
        }

        themeManager.getSettingsSubOptionsBackgroundDrawable()?.let {
            binding.allAges.background = it
            binding.ages26.background = it.copy()
            binding.ages7AndUp.background = it.copy()
            binding.ages7AndUpFantasy.background = it.copy()
            binding.parentalGuidanceRecommended.background = it.copy()
            binding.parentsStronglyCautioned.background = it.copy()
            binding.unsuitableUnder14.background = it.copy()
            binding.unsuitableUnder17.background = it.copy()
            binding.restricted.background = it.copy()
            binding.adultsOnly.background = it.copy()
            binding.unratedContent.background = it.copy()
            binding.resetAppSettings.background = it.copy()
            binding.changesSaved.background = it.copy()
        }
    }

    companion object {
        fun newInstance() = PermittedClassificationsFragment()
        const val KEY_ALL_AGES_SELECTED = "G"
        const val KEY_AGES26_SELECTED = "Y"
        const val KEY_AGES7ANDUP_SELECTED = "Y7"
        const val KEY_AGES7ANDUP_FANTASY_SELECTED = "Y7 FV"
        const val KEY_PARENTAL_GUIDANCE_SELECTED = "PG"
        const val KEY_PARENTS_CAUTIONED = "PG-13"
        const val KEY_UNSUITABLE_UNDER14 = "14"
        const val KEY_UNSUITABLE_UNDER17 = "MA"
        const val KEY_RESTRICTED = "R"
        const val KEY_ADULTS_ONLY_SELECTED = "NC-17"
        const val KEY_UNRATED_CONTENT_SELECTED = "UNRATED"
    }

    private fun handlePermittedClassificationSelection(position: Int) {
        permittedClassificationsViewModel.setPermittedClassifications(position)
    }

    private fun setZoomedImages(enabled: Boolean){
        setZoomProperties(binding.allAgesSelected, enabled)
        setZoomProperties(binding.ages26Selected, enabled)
        setZoomProperties(binding.ages7AndUpSelected, enabled)
        setZoomProperties(binding.ages7AndUpFantasySelected, enabled)
        setZoomProperties(binding.parentalGuidanceSelected, enabled)
        setZoomProperties(binding.parentsStronglyCautionedSelected, enabled)
        setZoomProperties(binding.unsuitableUnder14Selected, enabled)
        setZoomProperties(binding.unsuitableUnder17Selected, enabled)
        setZoomProperties(binding.restrictedSelected, enabled)
        setZoomProperties(binding.adultsOnlySelected, enabled)
        setZoomProperties(binding.unratedContentSelected, enabled)
        setZoomProperties(binding.changesSavedTickImage, enabled)
        }

   private fun setZoomProperties(imageview: ImageView, enabled: Boolean){
       imageview.layoutParams.height =
            resources.getDimension(if (enabled) R.dimen._30dp else R.dimen._25dp).toInt()
       imageview.layoutParams.width =
            resources.getDimension(if (enabled) R.dimen._40dp else R.dimen._35dp).toInt()
    }

}