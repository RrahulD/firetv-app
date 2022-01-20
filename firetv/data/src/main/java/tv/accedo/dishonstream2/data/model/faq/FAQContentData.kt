package tv.accedo.dishonstream2.data.model.faq

import tv.accedo.dishonstream2.domain.model.home.template.base.MetaData

data class FAQContentData(
    val meta: MetaData,
    val header: String,
    val paragraph: String
)