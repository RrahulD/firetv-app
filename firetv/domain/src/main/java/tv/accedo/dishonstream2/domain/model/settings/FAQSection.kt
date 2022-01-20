package tv.accedo.dishonstream2.domain.model.settings

data class FAQSection(
    val header: String,
    val faqs: List<FAQContent>
)