package tv.accedo.dishonstream2.data.datahelper

import org.json.JSONArray
import org.json.JSONObject
import tv.accedo.dishonstream2.data.model.faq.FAQData
import tv.accedo.dishonstream2.data.model.base.ImageData
import tv.accedo.dishonstream2.data.model.faq.FAQContentData
import tv.accedo.dishonstream2.data.model.faq.FAQSectionData
import tv.accedo.dishonstream2.data.model.template.hometemplate.GuestHomeTemplateData
import tv.accedo.dishonstream2.data.model.template.hometemplate.ResidentHomeTemplateData
import tv.accedo.dishonstream2.data.model.template.swimlane.SwimLaneWidgetContainerData
import tv.accedo.dishonstream2.data.model.template.swimlane.SwimLaneWidgetLargeContainerData
import tv.accedo.dishonstream2.data.model.template.widget.*
import tv.accedo.dishonstream2.domain.model.home.template.base.MetaData

// class responsible for mapping data received in json form from CMP to data layer entities
class CMPJsonDataHelper {

    fun mapToGuestHomeTemplateData(jsonObject: JSONObject): GuestHomeTemplateData =
        GuestHomeTemplateData(
            meta = mapToMetaData(jsonObject.getJSONObject("_meta")),
            widgetColumn1 = jsonObject.getString("widgetColumn1"),
            widgetColumn2 = jsonObject.getString("widgetColumn2"),
            widgetColumn3 = jsonObject.getString("widgetColumn3"),
            title = jsonObject.getString("title")
        )

    fun mapToResidentHomeTemplateData(jsonObject: JSONObject): ResidentHomeTemplateData =
        ResidentHomeTemplateData(
            meta = mapToMetaData(jsonObject.getJSONObject("_meta")),
            contentContainers = mapJsonArrayToStringList(jsonObject.getJSONArray("contentContainers")),
            heroCarouselItems = mapJsonArrayToStringList(jsonObject.getJSONArray("heroCarouselItems")),
            title = jsonObject.getString("title")
        )

    fun mapToSwimLaneWidgetLargeContainerData(jsonObject: JSONObject): SwimLaneWidgetLargeContainerData =
        SwimLaneWidgetLargeContainerData(
            meta = mapToMetaData(jsonObject.getJSONObject("_meta")),
            widgetContents = mapJsonArrayToStringList(jsonObject.getJSONArray("widgetContents")),
            title = jsonObject.getString("title")
        )

    fun mapToSwimLaneWidgetContainerData(jsonObject: JSONObject): SwimLaneWidgetContainerData =
        SwimLaneWidgetContainerData(
            meta = mapToMetaData(jsonObject.getJSONObject("_meta")),
            widgetContents = mapJsonArrayToStringList(jsonObject.getJSONArray("widgetContents")),
            title = jsonObject.getString("title")
        )

    fun mapToLiveChannelWidgetData(jsonObject: JSONObject): LiveChannelWidgetData =
        LiveChannelWidgetData(
            meta = mapToMetaData(jsonObject.getJSONObject("_meta")),
            channelId = jsonObject.getLong("channelId"),
            title = jsonObject.getString("title")
        )

    fun mapToMoreInfoWidgetData(jsonObject: JSONObject): MoreInfoWidgetData =
        MoreInfoWidgetData(
            meta = mapToMetaData(jsonObject.getJSONObject("_meta")),
            subTitle = jsonObject.getStringOrEmpty("subTitle"),
            title = jsonObject.getStringOrEmpty("title"),
            headerImage = mapToImageData(jsonObject.getJSONObject("headerImage")),
            contents = mapJsonArrayToStringList(jsonObject.getJSONArray("contents")),
            name = jsonObject.getStringOrEmpty("name"),
            icon = mapToImageData(jsonObject.getJSONObject("icon")),
            link = jsonObject.getStringOrEmpty("link"),
            contentTitle = jsonObject.getStringOrEmpty("contenttitle")
        )

    fun mapToNotificationWidgetData(jsonObject: JSONObject): NotificationWidgetData =
        NotificationWidgetData(
            meta = mapToMetaData(jsonObject.getJSONObject("_meta")),
            icon = mapToImageData(jsonObject.getJSONObject("icon")),
            link = jsonObject.getStringOrEmpty("link"),
            title = jsonObject.getStringOrEmpty("title"),
            description = jsonObject.getStringOrEmpty("description")
        )

    fun mapToSportsWidgetData(jsonObject: JSONObject): SportsWidgetData =
        SportsWidgetData(
            meta = mapToMetaData(jsonObject.getJSONObject("_meta")),
            title = jsonObject.getString("title")
        )

    fun mapToStaticAdWidgetData(jsonObject: JSONObject): StaticAdWidgetData =
        StaticAdWidgetData(
            meta = mapToMetaData(jsonObject.getJSONObject("_meta")),
            adImage = mapToImageData(jsonObject.getJSONObject("adimage")),
            subTitle = jsonObject.getStringOrEmpty("subtitle"),
            name = jsonObject.getStringOrEmpty("name"),
            icon = mapToImageData(jsonObject.getJSONObject("icon")),
            link = jsonObject.getStringOrEmpty("link"),
            title = jsonObject.getStringOrEmpty("title"),
            description = jsonObject.getStringOrEmpty("description")
        )

    fun mapToWeatherWidgetData(jsonObject: JSONObject): WeatherWidgetData =
        WeatherWidgetData(
            meta = mapToMetaData(jsonObject.getJSONObject("_meta")),
            title = jsonObject.getString("title")
        )

    fun mapToMoreInfoContentData(jsonObject: JSONObject): MoreInfoContentData =
        MoreInfoContentData(
            meta = mapToMetaData(jsonObject.getJSONObject("_meta")),
            header = jsonObject.getStringOrEmpty("header"),
            paragraph = jsonObject.getStringOrEmpty("paragraph"),
            image = if (jsonObject.has("image")) mapToImageData(jsonObject.getJSONObject("image")) else null
        )

    fun mapToFAQData(jsonObject: JSONObject): FAQData =
        FAQData(
            meta = mapToMetaData(jsonObject.getJSONObject("_meta")),
            sections = mapJsonArrayToStringList(jsonObject.getJSONArray("sections"))
        )

    fun mapToFAQSectionData(jsonObject: JSONObject): FAQSectionData =
        FAQSectionData(
            meta = mapToMetaData(jsonObject.getJSONObject("_meta")),
            header = jsonObject.getStringOrEmpty("header"),
            contents = mapJsonArrayToStringList(jsonObject.getJSONArray("contents"))
        )

    fun mapToFAQContentData(jsonObject: JSONObject): FAQContentData =
        FAQContentData(
            meta = mapToMetaData(jsonObject.getJSONObject("_meta")),
            header = jsonObject.getStringOrEmpty("header"),
            paragraph = jsonObject.getStringOrEmpty("paragraph"),
        )

    private fun mapToImageData(jsonObject: JSONObject): ImageData = ImageData(
        fileId = jsonObject.getString("fileId"),
        fileName = jsonObject.getString("fileName"),
        fileUrl = jsonObject.getString("fileUrl"),
        size = jsonObject.getLong("size")
    )

    private fun mapToMetaData(jsonObject: JSONObject): MetaData = MetaData(
        id = jsonObject.getString("id"),
        typeId = jsonObject.getString("typeId"),
        typeAlias = jsonObject.getString("typeAlias"),
        publishedFrom = jsonObject.getString("publishedFrom")
    )

    private fun mapJsonArrayToStringList(jsonArray: JSONArray): List<String> {
        val stringList = arrayListOf<String>()
        for (i in 0 until jsonArray.length())
            stringList.add(jsonArray.getString(i))
        return stringList
    }

    // extension
    private fun JSONObject.getStringOrEmpty(key: String) = if (this.has(key)) this.getString(key) else ""
}