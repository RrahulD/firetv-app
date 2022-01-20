package tv.accedo.dishonstream2.ui.main.settings.recordings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tv.accedo.dishonstream2.databinding.RecordingsSettingsFragmentBinding
import tv.accedo.dishonstream2.ui.base.BaseFragment

class RecordingsSettingFragment : BaseFragment() {

    private lateinit var binding: RecordingsSettingsFragmentBinding

    companion object {
        fun newInstance() = RecordingsSettingFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RecordingsSettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
}