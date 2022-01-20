package tv.accedo.dishonstream2.domain.datahelper

import tv.accedo.dishonstream2.domain.model.home.template.EntryEntity
import tv.accedo.dishonstream2.domain.model.settings.FAQ

interface FAQHelper {
    fun getFAQ(entries: Map<String, EntryEntity>, faqId: String): FAQ
}