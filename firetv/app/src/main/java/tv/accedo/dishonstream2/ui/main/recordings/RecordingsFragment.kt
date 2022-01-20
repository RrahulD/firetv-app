package tv.accedo.dishonstream2.ui.main.recordings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.androidx.viewmodel.ext.android.viewModel
import tv.accedo.dishonstream2.databinding.RecordingsFragmentBinding
import tv.accedo.dishonstream2.ui.base.BaseFragment

class RecordingsFragment : BaseFragment() {

    private lateinit var binding:RecordingsFragmentBinding
    private val vm: RecordingsViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = RecordingsFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {
        fun newInstance() = RecordingsFragment()
    }

}