package tv.accedo.dishonstream2.ui.main.settings

import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.SettingsFragmentBinding
import tv.accedo.dishonstream2.domain.usecase.settings.GetSettingsOptionsUseCase
import tv.accedo.dishonstream2.extensions.copy
import tv.accedo.dishonstream2.ui.base.BaseFragment
import tv.accedo.dishonstream2.ui.main.settings.account.AccountFragment
import tv.accedo.dishonstream2.ui.main.settings.appsettings.AppSettingsFragment
import tv.accedo.dishonstream2.ui.main.settings.faqs.FaqsFragment
import tv.accedo.dishonstream2.ui.main.settings.legalandabout.LegalAndAboutFragment
import tv.accedo.dishonstream2.ui.main.settings.parentalcontrols.ParentalControlsFragment
import tv.accedo.dishonstream2.ui.main.settings.recordings.RecordingsSettingFragment
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel
import tv.accedo.dishonstream2.ui.theme.ThemeManager


class SettingsFragment : BaseFragment() {
    private lateinit var binding: SettingsFragmentBinding
    val settingsViewModel: SettingsViewModel by viewModel()
    private lateinit var lastOptionSelected: TextView
    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()
    private val themeManager: ThemeManager by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SettingsFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recordings.setOnClickListener {
            if (::lastOptionSelected.isInitialized) {
                lastOptionSelected.typeface = Typeface.DEFAULT
            }
            binding.recordings.typeface = Typeface.DEFAULT_BOLD
            lastOptionSelected = binding.recordings
            replaceFragment(RecordingsSettingFragment.newInstance())
        }

        binding.account.setOnClickListener {
            if (::lastOptionSelected.isInitialized) {
                lastOptionSelected.typeface = Typeface.DEFAULT
            }
            binding.account.typeface = Typeface.DEFAULT_BOLD
            lastOptionSelected = binding.account
            replaceFragment(AccountFragment.newInstance())
        }

        binding.parentalControls.setOnClickListener {
            if (::lastOptionSelected.isInitialized) {
                lastOptionSelected.typeface = Typeface.DEFAULT
            }
            binding.parentalControls.typeface = Typeface.DEFAULT_BOLD
            lastOptionSelected = binding.parentalControls
            replaceFragment(ParentalControlsFragment.newInstance())
        }

        binding.appSettings.setOnClickListener {
            if (::lastOptionSelected.isInitialized) {
                lastOptionSelected.typeface = Typeface.DEFAULT
            }
            binding.appSettings.typeface = Typeface.DEFAULT_BOLD
            lastOptionSelected = binding.appSettings
            replaceFragment(AppSettingsFragment.newInstance())
        }

        binding.faqs.setOnClickListener {
            if (::lastOptionSelected.isInitialized) {
                lastOptionSelected.typeface = Typeface.DEFAULT
            }
            binding.faqs.typeface = Typeface.DEFAULT_BOLD
            lastOptionSelected = binding.faqs
            replaceFragment(FaqsFragment.newInstance())
        }

        binding.legalAndAbout.setOnClickListener {
            if (::lastOptionSelected.isInitialized) {
                lastOptionSelected.typeface = Typeface.DEFAULT
            }
            binding.legalAndAbout.typeface = Typeface.DEFAULT_BOLD
            lastOptionSelected = binding.legalAndAbout
            replaceFragment(LegalAndAboutFragment.newInstance())
        }

        binding.signOut.setOnClickListener {
            if (::lastOptionSelected.isInitialized) {
                lastOptionSelected.typeface = Typeface.DEFAULT
            }
            binding.signOut.typeface = Typeface.DEFAULT_BOLD
            lastOptionSelected = binding.signOut
        }

        settingsViewModel.optionsLiveData.observe(viewLifecycleOwner) { options ->
            for ((index, option) in options.withIndex()) {
                if (option == GetSettingsOptionsUseCase.RECORDINGS_KEY) {
                    if (index == 0) {
                        val lp = binding.recordings.layoutParams as ConstraintLayout.LayoutParams
                        lp.setMargins(0, 25, 0, 0)
                        binding.recordings.layoutParams = lp
                        binding.recordings.typeface = Typeface.DEFAULT_BOLD
                        lastOptionSelected = binding.recordings
                        replaceFragment(RecordingsSettingFragment.newInstance())
                    }
                    binding.recordings.visibility = View.VISIBLE
                }

                if (option == GetSettingsOptionsUseCase.ACCOUNT_KEY) {
                    if (index == 0) {
                        val lp = binding.account.layoutParams as ConstraintLayout.LayoutParams
                        lp.setMargins(0, 25, 0, 0)
                        binding.account.layoutParams = lp
                        binding.account.typeface = Typeface.DEFAULT_BOLD
                        lastOptionSelected = binding.account
                        replaceFragment(AccountFragment.newInstance())
                    }
                    binding.account.visibility = View.VISIBLE
                }

                if (option == GetSettingsOptionsUseCase.PARENTAL_CONTROLS_KEY) {
                    binding.parentalControls.visibility = View.VISIBLE
                    if (index == 0) {
                        val lp = binding.parentalControls.layoutParams as ConstraintLayout.LayoutParams
                        lp.setMargins(0, 25, 0, 0)
                        binding.parentalControls.layoutParams = lp
                        binding.parentalControls.typeface = Typeface.DEFAULT_BOLD
                        lastOptionSelected = binding.parentalControls
                        replaceFragment(ParentalControlsFragment.newInstance())
                        binding.parentalControls.requestFocus()
                    }

                }

                if (option == GetSettingsOptionsUseCase.APP_SETTINGS_KEY) {
                    binding.appSettings.visibility = View.VISIBLE
                    if (index == 0) {
                        val lp = binding.appSettings.layoutParams as ConstraintLayout.LayoutParams
                        lp.setMargins(0, 25, 0, 0)
                        binding.appSettings.layoutParams = lp
                        binding.appSettings.typeface = Typeface.DEFAULT_BOLD
                        lastOptionSelected = binding.appSettings
                        replaceFragment(AppSettingsFragment.newInstance())
                        binding.appSettings.requestFocus()
                    }else{
                        binding.appSettings.nextFocusUpId = R.id.parental_controls
                    }
                }

                if (option == GetSettingsOptionsUseCase.FAQS_KEY) {
                    if (index == 0) {
                        val lp = binding.faqs.layoutParams as ConstraintLayout.LayoutParams
                        lp.setMargins(0, 25, 0, 0)
                        binding.faqs.layoutParams = lp
                        binding.faqs.typeface = Typeface.DEFAULT_BOLD
                        lastOptionSelected = binding.faqs
                        replaceFragment(FaqsFragment.newInstance())
                    }
                    binding.faqs.visibility = View.VISIBLE
                }

                if (option == GetSettingsOptionsUseCase.LEGAL_AND_ABOUT_KEY) {
                    if (index == 0) {
                        val lp = binding.legalAndAbout.layoutParams as ConstraintLayout.LayoutParams
                        lp.setMargins(0, 25, 0, 0)
                        binding.legalAndAbout.layoutParams = lp
                        binding.legalAndAbout.typeface = Typeface.DEFAULT_BOLD
                        lastOptionSelected = binding.legalAndAbout
                        replaceFragment(LegalAndAboutFragment.newInstance())
                    }
                    binding.legalAndAbout.visibility = View.VISIBLE
                }

                if (option == GetSettingsOptionsUseCase.SIGN_OUT_KEY) {
                    if (index == 0) {
                        binding.signOut.typeface = Typeface.DEFAULT_BOLD
                        lastOptionSelected = binding.signOut
                    }
                    binding.signOut.visibility = View.VISIBLE
                }
            }
        }

        sharedAppViewModel.largeFontEnabledLiveData.observe(viewLifecycleOwner) { enabled ->
            setZoom(enabled)
        }

        themeManager.getSettingOptionsBackgroundDrawable()?.let {
            with(binding) {
                appSettings.background = it
                parentalControls.background = it.copy()
                faqs.background = it.copy()
                legalAndAbout.background = it.copy()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && subMenuHasFocus()) {
            val fragment = childFragmentManager.fragments.firstOrNull()
            if (fragment?.view != null) {
                fragment.view?.requestFocus()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed(): Boolean {
        val fragment = childFragmentManager.fragments.firstOrNull()
        return if (fragment is ParentalControlsFragment || fragment is AppSettingsFragment || fragment is FaqsFragment || fragment is LegalAndAboutFragment)
            super.onBackPressed()
        else {
            lastOptionSelected.performClick()
            true
        }
    }

    private fun subMenuHasFocus(): Boolean {
        return binding.parentalControls.hasFocus()
            || binding.appSettings.hasFocus()
            || binding.faqs.hasFocus()
            || binding.legalAndAbout.hasFocus()
            || binding.recordings.hasFocus()
            || binding.account.hasFocus()
            || binding.signOut.hasFocus()
    }

    fun replaceFragment(fragment: Fragment) {
        childFragmentManager.commit {
            replace(R.id.frame_layout, fragment)
        }
    }

    private fun setZoom(value: Boolean) {
        binding.settingsTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (value) 30f else 26f)
        binding.appSettings.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (value) 15f else 13f)
        binding.faqs.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (value) 15f else 13f)
        binding.legalAndAbout.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (value) 15f else 13f)
        binding.account.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (value) 15f else 13f)
        binding.parentalControls.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (value) 15f else 13f)
        binding.recordings.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (value) 15f else 13f)
        binding.signOut.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (value) 15f else 13f)
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}