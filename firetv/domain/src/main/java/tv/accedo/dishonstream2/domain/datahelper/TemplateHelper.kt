package tv.accedo.dishonstream2.domain.datahelper

import tv.accedo.dishonstream2.domain.model.home.template.EntryEntity
import tv.accedo.dishonstream2.domain.model.home.template.hometemplate.HomeTemplate

interface TemplateHelper {
    fun getHomeScreenTemplate(entries: Map<String, EntryEntity>, homeTemplateId: String): HomeTemplate
}