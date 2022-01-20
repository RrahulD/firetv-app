package tv.accedo.dishonstream2.ui.main.ondemand

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.KeyEvent
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.ActivityOnDemandDetailsBinding
import tv.accedo.dishonstream2.domain.model.vod.*
import tv.accedo.dishonstream2.domain.util.AppConstants
import tv.accedo.dishonstream2.extensions.copy
import tv.accedo.dishonstream2.extensions.hide
import tv.accedo.dishonstream2.extensions.show
import tv.accedo.dishonstream2.ui.base.BaseActivity
import tv.accedo.dishonstream2.ui.base.BaseFragment

import tv.accedo.dishonstream2.ui.main.ondemand.seasondetails.SeasonEpisodeDetailsFragment
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel
import tv.accedo.dishonstream2.ui.theme.ThemeManager

class OnDemandDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityOnDemandDetailsBinding
    private val viewModel: OnDemandDetailsViewModel by viewModel()
    private val sharedAppViewModel: SharedAppViewModel by viewModel()
    private val themeManager: ThemeManager by inject()
    private var title = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnDemandDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        themeManager.getPrimaryButtonBackgroundDrawable()?.let {
            binding.playBtnParentView.background = it
            binding.seasonBtnParentView.background = it.copy()
        }
        if (intent?.hasExtra(KEY_ON_DEMAND_ITEM) == true) {
            when (val item = intent?.extras?.get(KEY_ON_DEMAND_ITEM)) {
                is Movie -> {
                    viewModel.getMovieDetails(item.id)
                }
                is Show -> {
                    viewModel.getShowDetails(item.id)
                    viewModel.getEpisodeDetails(item.id, DEFAULT_SEASON_NO, DEFAULT_EPISODE_NO)
                }
                else -> {
                    // no implementation required
                }
            }
        }
        viewModel.movieDetailsLiveData.observe(this) {
            binding.progressBar.hide()
            setItemDetails(it)
        }
        viewModel.showDetailsLiveData.observe(this) {
            binding.progressBar.hide()
            setItemDetails(it)
        }

        viewModel.episodeDetailsLiveData.observe(this) {
            Timber.tag(AppConstants.OnDemand.TAG).i("Show providers count : ${it.size}")
            binding.playBtnParentView.show()
            binding.seasonBtnParentView.show()
            binding.playBtnParentView.requestFocus()
            setPlayButtonClick(
                "$title " +
                    getString(R.string.on_demand_play_series_description_text).format(
                        DEFAULT_SEASON_NO,
                        DEFAULT_EPISODE_NO
                    ), it
            )
        }
        sharedAppViewModel.largeFontEnabledLiveData.observe(this) { enabled ->
            if (enabled) setZoom()
        }
    }

    private fun setItemDetails(item: Any) {
        var description = ""
        var ratingsGenres = ""
        var playBtnText = ""
        when (item) {
            is MovieDetails -> {
                title = item.title
                description = item.overview
                setPosterImage(item.backDropUrl)
                val stringBuilder = StringBuilder()
                stringBuilder.append(item.classification)
                stringBuilder.append(" ")
                stringBuilder.append(getString(R.string.bullet_char))
                stringBuilder.append(" ")
                stringBuilder.append(item.runTime)
                stringBuilder.append(" ")
                stringBuilder.append(getString(R.string.text_mins))
                stringBuilder.append(" ")
                stringBuilder.append(getString(R.string.bullet_char))
                stringBuilder.append(" ")
                stringBuilder.append(viewModel.getGenres(item.genres))
                stringBuilder.append(" ")
                stringBuilder.append(getString(R.string.bullet_char))
                stringBuilder.append(" ")
                stringBuilder.append(viewModel.getPartners(item.vodServices))
                ratingsGenres = stringBuilder.toString()
                playBtnText = getString(R.string.play_movie_text)
                binding.playBtnParentView.nextFocusDownId = binding.playBtnParentView.id
                binding.playBtnParentView.show()
                binding.playBtnParentView.requestFocus()
                setPlayButtonClick(title, item.vodServices)
            }
            is ShowDetails -> {
                title = item.title
                description = item.overview
                setPosterImage(item.backDropUrl)
                val stringBuilder = StringBuilder()
                stringBuilder.append(item.classification)
                stringBuilder.append(" ")
                stringBuilder.append(getString(R.string.bullet_char))
                stringBuilder.append(" ")
                stringBuilder.append(viewModel.getGenres(item.genres))
                ratingsGenres = stringBuilder.toString()
                playBtnText = getString(R.string.play_season_show_text).format(DEFAULT_SEASON_NO, DEFAULT_EPISODE_NO)
                binding.playBtnParentView.nextFocusDownId = binding.seasonBtnParentView.id
                binding.seasonBtnParentView.setOnClickListener {
                    showSeasonDetailsFragment(item)
                }
            }
            else -> {
                // no implementation required
            }
        }
        binding.title.text = title
        binding.description.text = description
        binding.ratingGenres.text = ratingsGenres
        binding.playButtonText.text = playBtnText
    }

    private fun setPosterImage(posterImage: String) {
        Glide.with(binding.root)
            .load(posterImage)
            .into(binding.poster)
    }

    private fun showSeasonDetailsFragment(showDetails: ShowDetails) {
        val fragment = SeasonEpisodeDetailsFragment.newInstance(showDetails)
        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, fragment, SeasonEpisodeDetailsFragment::class.java.simpleName)
            .addToBackStack(null)
            .commit()
    }

    private fun setPlayButtonClick(title: String, partnersList: List<VODService>?) {
        binding.playBtnParentView.setOnClickListener {
            if (partnersList?.isNotEmpty() == true) {
                OnDemandWatchDialogFragment.newInstance(title, partnersList).show(
                    supportFragmentManager
                )
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val fragment = getTopFragment()
        if (keyCode != KeyEvent.KEYCODE_BACK && fragment is BaseFragment) {
            return fragment.onKeyDown(keyCode, event)
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun setZoom() {
        binding.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35f)
        binding.ratingGenres.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
        binding.ratingGenres.maxLines = 2
        binding.description.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
        binding.description.maxLines = 4
        binding.playButtonText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        binding.seasonButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
    }

    companion object {
        private const val KEY_ON_DEMAND_ITEM = "onDemandItem"
        private const val DEFAULT_SEASON_NO = 1
        private const val DEFAULT_EPISODE_NO = 1

        fun startActivity(activity: FragmentActivity, item: VODContent) {
            val intent = Intent(activity, OnDemandDetailsActivity::class.java)
            intent.putExtra(KEY_ON_DEMAND_ITEM, item)
            activity.startActivity(intent)
        }
    }
}