package tv.accedo.dishonstream2.ui.main.home.sports

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import tv.accedo.dishonstream2.databinding.SportsFragmentBinding
import tv.accedo.dishonstream2.extensions.show
import tv.accedo.dishonstream2.ui.base.BaseFragment
import tv.accedo.dishonstream2.ui.main.MainFragment
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.factory.WidgetViewHolderFactory
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.util.WidgetUIHelper
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel

class SportsFragment : BaseFragment() {
    companion object {
        fun newInstance(): SportsFragment = SportsFragment()
    }

    lateinit var binding: SportsFragmentBinding
    private val sportsViewModel: SportsViewModel by viewModel()
    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()
    private val widgetViewHolderFactory: WidgetViewHolderFactory by inject()
    private val widgetUIHelper: WidgetUIHelper by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = SportsFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sportsViewModel.loadingLiveData.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it
        }

        sportsViewModel.errorLiveData.observe(viewLifecycleOwner) {
            binding.errorSection.show()
        }

        sportsViewModel.leagueGamesLiveData.observe(viewLifecycleOwner) {
            binding.leagues.adapter = SportsAdapter(
                widgetViewHolderFactory,
                widgetUIHelper,
                this,
                it,
                sharedAppViewModel.largeFontEnabledLiveData.value ?: false
            )
            binding.leagues.requestFocus()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && parentFragment is MainFragment
            && (parentFragment as MainFragment).isHeaderBarFocussed()
        ) {
            binding.leagues.requestFocus()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}