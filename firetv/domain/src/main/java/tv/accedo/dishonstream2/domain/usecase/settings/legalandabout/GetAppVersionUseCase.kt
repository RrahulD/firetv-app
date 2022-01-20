package tv.accedo.dishonstream2.domain.usecase.settings.legalandabout

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class GetAppVersionUseCase(
    private val context: Context
) {
    suspend operator fun invoke(): String = withContext(Dispatchers.IO) {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }
}