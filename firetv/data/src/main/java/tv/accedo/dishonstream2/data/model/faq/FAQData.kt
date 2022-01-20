package tv.accedo.dishonstream2.data.model.faq

import tv.accedo.dishonstream2.domain.model.home.template.base.MetaData

data class FAQData(
    val meta: MetaData,
    val sections: List<String>
)