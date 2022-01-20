package tv.accedo.dishonstream2.ui.main.home

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import tv.accedo.dishonstream2.databinding.HomeFragmentBinding
import tv.accedo.dishonstream2.domain.model.home.template.hometemplate.GuestHomeTemplate
import tv.accedo.dishonstream2.domain.model.home.template.hometemplate.ResidentHomeTemplate
import tv.accedo.dishonstream2.ui.base.BaseFragment
import tv.accedo.dishonstream2.ui.main.home.template.guest.GuestHomeTemplateScreen
import tv.accedo.dishonstream2.ui.main.home.template.resident.ResidentHomeTemplateScreen
import tv.accedo.dishonstream2.ui.main.home.template.resident.viewholder.carousel.ResidentHomeScreenCarouselViewHolder
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel
import java.lang.ref.WeakReference

class HomeFragment : BaseFragment() {

    lateinit var binding: HomeFragmentBinding
    private val viewModel: HomeViewModel by viewModel()
    private val sharedAppViewModel: SharedAppViewModel by sharedViewModel()
    private var residentHomeTemplateScreen: WeakReference<ResidentHomeTemplateScreen>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = HomeFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.errorLiveData.observe(viewLifecycleOwner, {
            showFatalErrorDialog("Error", it.message ?: "Something went wrong. Please close the app and open again later")
            Firebase.crashlytics.recordException(it)
        })

        viewModel.loadingLiveData.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it
        }

        viewModel.homePageTemplate.observe(viewLifecycleOwner) { homeTemplate ->
            binding.contentRoot.addView(
                when (homeTemplate) {
                    is GuestHomeTemplate -> GuestHomeTemplateScreen(this, homeTemplate).view
                    is ResidentHomeTemplate -> {
                        residentHomeTemplateScreen = WeakReference(
                            ResidentHomeTemplateScreen(
                                this,
                                homeTemplate,
                                sharedAppViewModel.largeFontEnabledLiveData.value ?: false
                            )
                        )
                        residentHomeTemplateScreen?.get()?.view
                    }
                }
            )
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            return handleFirstRailFocus()
        }
        return false
    }

    private fun handleFirstRailFocus(): Boolean {
        residentHomeTemplateScreen?.get()?.getRecyclerView()?.let {
            if (it.childCount < 2) {
                return false
            }
            val viewHolder: RecyclerView.ViewHolder? = it.getChildViewHolder(it.getChildAt(0))
            if (viewHolder is ResidentHomeScreenCarouselViewHolder? && viewHolder?.itemView?.hasFocus() == true) {
                it.getChildViewHolder(it.getChildAt(1)).itemView.requestFocus()
                return true
            }
        }
        return false
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}