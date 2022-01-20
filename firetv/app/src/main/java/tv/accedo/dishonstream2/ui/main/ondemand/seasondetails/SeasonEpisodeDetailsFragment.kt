package tv.accedo.dishonstream2.ui.main.ondemand.seasondetails

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import tv.accedo.dishonstream2.databinding.FragmentSeasonEpisodeDetailsBinding
import tv.accedo.dishonstream2.domain.model.vod.ShowDetails
import tv.accedo.dishonstream2.extensions.show
import tv.accedo.dishonstream2.ui.base.BaseFragment
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel
import tv.accedo.dishonstream2.ui.main.tvguide.view.CenterLayoutManager

class SeasonEpisodeDetailsFragment : BaseFragment() {
    private lateinit var binding: FragmentSeasonEpisodeDetailsBinding
    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()
    private val viewModel: SeasonDetailsViewModel by viewModel()
    private lateinit var showDetails: ShowDetails
    private var seasonId: Int = 0
    private var lastClickedSeasonPos: Int = 0

    companion object {
        private const val TAG = "SeasonEpisodeDetailsFragment :: "
        private const val KEY_SHOW_DETAILS = "ShowDetails"
        private const val FOCUS_DELAY = 100L
        fun newInstance(showDetails: ShowDetails): SeasonEpisodeDetailsFragment {
            return SeasonEpisodeDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_SHOW_DETAILS, showDetails)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSeasonEpisodeDetailsBinding.inflate(inflater)
        return binding.root
    }

    private val focusChangeListener =
        View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val focussedPos = binding.seasonRecyclerView.getChildLayoutPosition(binding.seasonRecyclerView.focusedChild)
                val firstVisibleItemPos =
                    (binding.seasonRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                val lastVisibleItemPos =
                    (binding.seasonRecyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                if (focussedPos >= lastVisibleItemPos - 1 || focussedPos <= firstVisibleItemPos + 1)
                    binding.seasonRecyclerView.smoothScrollToPosition(focussedPos)
            }
        }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            showDetails = arguments?.getParcelable<ShowDetails>(KEY_SHOW_DETAILS) as ShowDetails
            binding.seasonRecyclerView.layoutManager = CenterLayoutManager(binding.root.context)
            binding.seasonDetailsRecyclerView.layoutManager =
                CenterLayoutManager(binding.root.context)
            binding.titleText.text = showDetails.title
            binding.seasonRecyclerView.adapter = SeasonListAdapter(
                showDetails.seasons,
                sharedAppViewModel.largeFontEnabledLiveData.value ?: false, focusChangeListener
            ) { seasonId, position ->
                lastClickedSeasonPos = position
                getSeasonDetails(seasonId)
            }
            binding.seasonRecyclerView.requestFocus()
            getSeasonDetails(showDetails.seasons[0])
            viewModel.loadingLiveData.observe(viewLifecycleOwner) {
                binding.progressBar.isVisible = it
            }

            viewModel.episodeDetailsLiveData.observe(viewLifecycleOwner) { episodeList ->
                binding.seasonDetailsRecyclerView.show()
                binding.seasonDetailsRecyclerView.adapter =
                    EpisodeListAdapter(
                        episodeList,
                        this,
                        sharedAppViewModel.largeFontEnabledLiveData.value ?: false,
                        seasonId,
                        showDetails, focusChangeListener
                    )
                binding.seasonDetailsRecyclerView.adapter?.notifyDataSetChanged()
            }


            viewModel.errorLiveData.observe(viewLifecycleOwner) {
                binding.sessionErrorSection.show()
            }

        } catch (ex: Exception) {
            Timber.tag(TAG).e("Exception in SeasonEpisodeDetailsFragment : $ex")
        }
    }

    private fun getSeasonDetails(seasonId: Int) {
        binding.seasonDetailsRecyclerView.visibility = View.INVISIBLE
        this.seasonId = seasonId
        viewModel.getSeasonDetails(showDetails.id, seasonId)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && isEpisodeFocussed()) {
            binding.seasonRecyclerView.smoothScrollToPosition(lastClickedSeasonPos)
            binding.seasonRecyclerView.postDelayed({
                val btnView: AppCompatButton? = binding.seasonRecyclerView.getChildAt(lastClickedSeasonPos) as AppCompatButton?
                btnView?.requestFocus() ?: binding.seasonRecyclerView.requestFocus()
            }, FOCUS_DELAY)
            return true
        } else if ((keyCode == KeyEvent.KEYCODE_DPAD_DOWN && isLastItemFocussed())
            || (keyCode == KeyEvent.KEYCODE_DPAD_UP && isFirstItemFocussed())
        ) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun isEpisodeFocussed(): Boolean {
        return binding.seasonDetailsRecyclerView.getChildLayoutPosition(binding.seasonDetailsRecyclerView.focusedChild) >= 0
    }

    private fun isLastItemFocussed(): Boolean {
        return binding.seasonRecyclerView.getChildLayoutPosition(binding.seasonRecyclerView.focusedChild) == binding.seasonRecyclerView.adapter?.itemCount?.minus(
            1
        ) || binding.seasonDetailsRecyclerView.getChildLayoutPosition(binding.seasonDetailsRecyclerView.focusedChild) == binding.seasonDetailsRecyclerView.adapter?.itemCount?.minus(
            1
        )
    }

    private fun isFirstItemFocussed(): Boolean {
        return binding.seasonRecyclerView.getChildLayoutPosition(binding.seasonRecyclerView.focusedChild) == 0
            || binding.seasonDetailsRecyclerView.getChildLayoutPosition(binding.seasonDetailsRecyclerView.focusedChild) == 0
    }
}

