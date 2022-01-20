package tv.accedo.dishonstream2.domain.usecase.home

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tv.accedo.dishonstream2.domain.datahelper.TemplateHelper
import tv.accedo.dishonstream2.domain.model.home.template.hometemplate.HomeTemplate
import tv.accedo.dishonstream2.domain.repository.CMPRepository

class GetHomeTemplateUseCase(
    private val cmpRepository: CMPRepository,
    private val templateHelper: TemplateHelper
) {
    companion object {
        const val HOME_TEMPLATE_FIRE_TV = "homeTemplateFireTv"
    }

    suspend operator fun invoke(): HomeTemplate = withContext(Dispatchers.IO) {
        val homeTemplateId = cmpRepository.getMetadata(HOME_TEMPLATE_FIRE_TV)
        templateHelper.getHomeScreenTemplate(cmpRepository.getAllEntries(), homeTemplateId)
    }
}