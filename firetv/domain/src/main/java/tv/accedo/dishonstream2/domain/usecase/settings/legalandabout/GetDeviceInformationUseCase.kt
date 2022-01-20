package tv.accedo.dishonstream2.domain.usecase.settings.legalandabout

import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetDeviceInformationUseCase {
    suspend operator fun invoke(): String = withContext(Dispatchers.Default) {
        "${Build.BRAND} ${Build.MODEL} OS Version : ${Build.VERSION.RELEASE}"
    }
}