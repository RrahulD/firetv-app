package tv.accedo.dishonstream2.data.model.faq

import tv.accedo.dishonstream2.domain.model.home.template.base.MetaData

data class FAQSectionData(
    val meta: MetaData,
    val header: String,
    val contents: List<String>
)