package tv.accedo.dishonstream2.ui.main.ondemand

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.DialogOnDemandPlayOptionScreenBinding
import tv.accedo.dishonstream2.domain.model.vod.VODService
import tv.accedo.dishonstream2.domain.util.AppConstants
import tv.accedo.dishonstream2.ui.base.BaseFullScreenDialogFragment
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel
import tv.accedo.dishonstream2.ui.main.tvguide.view.CenterLayoutManager
import tv.accedo.dishonstream2.ui.theme.ThemeManager


class OnDemandWatchDialogFragment : BaseFullScreenDialogFragment() {

    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()
    private val themeManager: ThemeManager by inject()
    private lateinit var binding: DialogOnDemandPlayOptionScreenBinding

    private val focusChangeListener =
        View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val focussedPos = binding.partnersRecyclerView.getChildLayoutPosition(binding.partnersRecyclerView.focusedChild)
                val firstVisibleItemPos =
                    (binding.partnersRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                val lastVisibleItemPos =
                    (binding.partnersRecyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                if (focussedPos >= lastVisibleItemPos - SCROLLING_OFFSET || focussedPos <= firstVisibleItemPos + SCROLLING_OFFSET)
                    binding.partnersRecyclerView.smoothScrollToPosition(focussedPos)
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogOnDemandPlayOptionScreenBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments?.containsKey(KEY_PARTNERS_LIST) == true) {
            val data: ArrayList<VODService>? = arguments?.getParcelableArrayList(KEY_PARTNERS_LIST)
            showPartnersButtons(data)
        }
        val title = arguments?.getString(KEY_TITLE, "") ?: ""
        binding.description.text = getString(R.string.on_demand_play_option_dialog_description).format(title)
        sharedAppViewModel.largeFontEnabledLiveData.observe(viewLifecycleOwner) { enabled ->
            if (enabled) setZoom()
        }
    }

    private fun showPartnersButtons(partners: List<VODService>?) {
        if (partners.isNullOrEmpty()) {
            return
        }
        binding.partnersRecyclerView.layoutManager = CenterLayoutManager(requireContext())
        binding.partnersRecyclerView.adapter =
            PlayProvidersAdapter(
                partners.filter {
                    // the google play will not be added in the partner list as we can't trigger it from amazon fire stick
                    !it.name.equals(PARTNER_GOOGLE_PLAY, true)
                },
                sharedAppViewModel.largeFontEnabledLiveData.value ?: false,
                focusChangeListener,
                vodProviderClickListener
            )
        binding.partnersRecyclerView.requestFocus()
    }

    private val vodProviderClickListener: ((VODService) -> Unit) = {
        // Deeplink to the partner
        Timber.tag(AppConstants.OnDemand.TAG).i("Playing content via partner ${it.name}, deepLink : ${it.deeplinkUrl}")
        try {
            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it.deeplinkUrl))
            val activities = requireActivity().packageManager.queryIntentActivities(mapIntent, 0)
            val isIntentSafe = activities.size > 0
            if (isIntentSafe) {
                startActivity(mapIntent)
            } else {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(MARKET_AMAZON_URL + it.deeplinkUrl)
                startActivity(intent)
            }
        } catch (e: Exception) {
            Timber.tag(AppConstants.OnDemand.TAG).i("Exception while deeplinking ${e.toString()}")
            searchAppInBrowser(it.deeplinkUrl)
        }
    }

    private fun searchAppInBrowser(deeplink: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(deeplink)
        startActivity(intent)
    }

    private fun setZoom() {
        binding.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 29f)
        binding.description.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
    }

    companion object {
        private const val KEY_PARTNERS_LIST = "partnersList"
        private const val KEY_TITLE = "title"
        private const val MARKET_AMAZON_URL = "amzn://apps/android?p="
        private const val WEB_AMAZON_URL = "http://www.amazon.com/gp/mas/dl/android?p="
        private const val PARTNER_GOOGLE_PLAY = "Google Play"
        private const val SCROLLING_OFFSET = 3
        fun newInstance(title: String, partnersList: List<VODService>) = OnDemandWatchDialogFragment().apply {
            arguments = Bundle().apply {
                putString(KEY_TITLE, title)
                putParcelableArrayList(KEY_PARTNERS_LIST, partnersList as ArrayList<out Parcelable>)
            }
        }
    }
}