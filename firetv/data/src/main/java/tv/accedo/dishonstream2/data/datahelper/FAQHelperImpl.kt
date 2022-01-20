package tv.accedo.dishonstream2.data.datahelper

import tv.accedo.dishonstream2.domain.datahelper.FAQHelper
import tv.accedo.dishonstream2.domain.model.home.template.EntryEntity
import tv.accedo.dishonstream2.domain.model.home.template.EntryType
import tv.accedo.dishonstream2.domain.model.settings.FAQ
import tv.accedo.dishonstream2.domain.model.settings.FAQContent
import tv.accedo.dishonstream2.domain.model.settings.FAQSection

class FAQHelperImpl(
    private val cmpJsonDataHelper: CMPJsonDataHelper
) : FAQHelper {
    private lateinit var entries: Map<String, EntryEntity>

    override fun getFAQ(entries: Map<String, EntryEntity>, faqId: String): FAQ {
        this.entries = entries
        val faqEntry = getEntryById(faqId)
        if (faqEntry.entryType != EntryType.FAQ)
            throw RuntimeException("Unable to parse FAQ data")

        val faqData = cmpJsonDataHelper.mapToFAQData(faqEntry.entryData)
        val sections = faqData.sections.map { sectionId -> getFQASectionById(sectionId) }
        return FAQ(sections)
    }

    private fun getFQASectionById(sectionId: String): FAQSection {
        val sectionEntry = getEntryById(sectionId)
        val sectionData = cmpJsonDataHelper.mapToFAQSectionData(sectionEntry.entryData)
        val faqs = sectionData.contents.map { contentId -> getFQAContentByID(contentId) }
        return FAQSection(
            header = sectionData.header,
            faqs = faqs
        )
    }

    private fun getFQAContentByID(contentId: String): FAQContent {
        val faqContentEntry = getEntryById(contentId)
        val faqContentData = cmpJsonDataHelper.mapToFAQContentData(faqContentEntry.entryData)
        return FAQContent(
            question = faqContentData.header,
            answer = faqContentData.paragraph
        )
    }

    private fun getEntryById(id: String): EntryEntity {
        return this.entries.getValue(id)
    }
}