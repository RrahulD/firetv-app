package tv.accedo.dishonstream2.domain.usecase.home

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tv.accedo.dishonstream2.domain.model.home.template.EntryType
import tv.accedo.dishonstream2.domain.repository.CMPRepository
import tv.accedo.dishonstream2.domain.usecase.home.GetHomeTemplateUseCase.Companion.HOME_TEMPLATE_FIRE_TV

enum class HomeTemplateType {
    GUEST, RESIDENT
}

class GetHomeTemplateTypeUseCase(
    private val cmpRepository: CMPRepository
) {
    suspend operator fun invoke(): HomeTemplateType = withContext(Dispatchers.IO) {
        val homeTemplateId = cmpRepository.getMetadata(HOME_TEMPLATE_FIRE_TV)
        val entry = cmpRepository.getEntryById(homeTemplateId)
        if (entry.entryType == EntryType.RESIDENT_HOME_TEMPLATE)
            HomeTemplateType.RESIDENT else HomeTemplateType.GUEST
    }
}