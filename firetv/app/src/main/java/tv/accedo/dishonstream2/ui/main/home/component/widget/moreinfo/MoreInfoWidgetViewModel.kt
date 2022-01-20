package tv.accedo.dishonstream2.ui.main.home.component.widget.moreinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import timber.log.Timber
import tv.accedo.dishonstream2.domain.model.supair.Channel
import tv.accedo.dishonstream2.domain.usecase.channel.GetChannelByChannelIdUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.parentalcontrols.GetPermittedClassificationsUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.parentalcontrols.createpin.GetParentalControlPinUseCase
import tv.accedo.dishonstream2.ui.base.viewholder.BaseViewHolderViewModel
import tv.accedo.dishonstream2.ui.main.settings.parentalcontrols.permittedclassifications.PermittedClassificationsViewModel

@OptIn(KoinApiExtension::class)
class MoreInfoWidgetViewModel(
    private val getChannelByChannelIdUseCase: GetChannelByChannelIdUseCase,
    override val viewHolderScope: CoroutineScope
) : BaseViewHolderViewModel, KoinComponent {

    suspend fun getChannel(channelId: Long): Channel? {
        return withContext(Dispatchers.Default) {
            getChannelByChannelIdUseCase(channelId)
        }
    }

}