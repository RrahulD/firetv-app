package tv.accedo.dishonstream2.data.model.template.hometemplate

import tv.accedo.dishonstream2.domain.model.home.template.base.MetaData

data class ResidentHomeTemplateData(
    val contentContainers: List<String>,
    val heroCarouselItems: List<String>,
    val meta: MetaData,
    val title: String
)

