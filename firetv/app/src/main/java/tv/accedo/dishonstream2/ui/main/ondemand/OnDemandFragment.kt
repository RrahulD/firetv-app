package tv.accedo.dishonstream2.ui.main.ondemand

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import tv.accedo.dishonstream2.databinding.OnDemandFragmentBinding
import tv.accedo.dishonstream2.domain.usecase.home.Tab
import tv.accedo.dishonstream2.extensions.hide
import tv.accedo.dishonstream2.extensions.show
import tv.accedo.dishonstream2.ui.base.BaseFragment
import tv.accedo.dishonstream2.ui.main.MainFragment
import tv.accedo.dishonstream2.ui.main.search.SearchItemAdapter
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel
import tv.accedo.dishonstream2.ui.view.NavigationView

class OnDemandFragment : BaseFragment() {

    private lateinit var binding: OnDemandFragmentBinding
    private val viewModel: OnDemandViewModel by viewModel()
    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = OnDemandFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.show()
        viewModel.fetchOnDemandData()

        viewModel.onDemandResultsLiveData.observe(viewLifecycleOwner) { results ->
            binding.onDemandRecyclerView.adapter =
                SearchItemAdapter(results, this, sharedAppViewModel.largeFontEnabledLiveData.value ?: false)
            binding.progressBar.hide()
            binding.recyclerViewParent.show()
            binding.onDemandRecyclerView?.adapter?.notifyDataSetChanged()
            binding.recyclerViewParent.post {
                binding.recyclerViewParent.requestFocus()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && parentFragment is MainFragment
            && (parentFragment as MainFragment).isHeaderBarFocussed()
        ) {
            binding.onDemandRecyclerView.getChildAt(0)?.requestFocus()
            return true
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP && isMoveFocusOnDemand()) {
            getNavigationView()?.requestFocusOnTab(Tab.ON_DEMAND)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun getNavigationView(): NavigationView? {
        return if (parentFragment is MainFragment) (parentFragment as MainFragment).getNavigationView() else null
    }

    private fun isMoveFocusOnDemand(): Boolean {
        val focussedPos = binding.onDemandRecyclerView.getChildLayoutPosition(binding.onDemandRecyclerView.focusedChild)
        return focussedPos == 0
    }

    companion object {
        fun newInstance() = OnDemandFragment()
    }

}