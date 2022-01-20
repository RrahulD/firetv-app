package tv.accedo.dishonstream2.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.ext.android.viewModel
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.data.exception.NoSmartBoxIdFoundException
import tv.accedo.dishonstream2.databinding.SplashFragmentBinding
import tv.accedo.dishonstream2.extensions.hide
import tv.accedo.dishonstream2.ui.base.BaseFragment
import tv.accedo.dishonstream2.ui.main.MainActivity
import tv.accedo.dishonstream2.ui.main.MainFragment
import java.net.SocketTimeoutException

class SplashFragment : BaseFragment() {

    companion object {
        fun newInstance() = SplashFragment()
    }

    private val vm: SplashViewModel by viewModel()
    private lateinit var binding: SplashFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SplashFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.appInitStateLivedata.observe(viewLifecycleOwner) { isInitialized ->
            if (isInitialized) {
                val activity = requireActivity()
                if (activity is MainActivity) activity.addFragment(MainFragment.newInstance())
            }
        }

        vm.errorLiveData.observe(viewLifecycleOwner) { error ->
            binding.progressBar.hide()
            when (error) {
                is SocketTimeoutException -> showFatalErrorDialog(
                    getString(R.string.network_error),
                    getString(R.string.network_error_message),
                )

                is NoSmartBoxIdFoundException -> showFatalErrorDialog(
                    getString(R.string.no_smartbox_found_error),
                    getString(R.string.no_smartbox_found_error_message),
                )

                else -> showFatalErrorDialog(
                    getString(R.string.unknown_error),
                    getString(R.string.unknown_error_message),
                )
            }
            Firebase.crashlytics.recordException(error)
        }

        vm.propertyLogoUrlLiveData.observe(viewLifecycleOwner) { url ->
            Glide.with(this)
                .load(url)
                .into(binding.imageView)
        }

        vm.initializeApp()
    }
}