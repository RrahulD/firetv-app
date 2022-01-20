package tv.accedo.dishonstream2.data.datahelper

import org.json.JSONObject
import tv.accedo.dishonstream2.domain.datahelper.TemplateHelper
import tv.accedo.dishonstream2.domain.model.home.template.EntryEntity
import tv.accedo.dishonstream2.domain.model.home.template.EntryType
import tv.accedo.dishonstream2.domain.model.home.template.base.Image
import tv.accedo.dishonstream2.domain.model.home.template.hometemplate.GuestHomeTemplate
import tv.accedo.dishonstream2.domain.model.home.template.hometemplate.HomeTemplate
import tv.accedo.dishonstream2.domain.model.home.template.hometemplate.ResidentHomeTemplate
import tv.accedo.dishonstream2.domain.model.home.template.other.MoreInfoContent
import tv.accedo.dishonstream2.domain.model.home.template.swimlane.SwimLaneContainer
import tv.accedo.dishonstream2.domain.model.home.template.swimlane.SwimLaneType
import tv.accedo.dishonstream2.domain.model.home.template.widget.*

class TemplateHelperImpl(
    private val cmpJsonDataHelper: CMPJsonDataHelper,
) : TemplateHelper {
    private lateinit var entries: Map<String, EntryEntity>

    override fun getHomeScreenTemplate(entries: Map<String, EntryEntity>, homeTemplateId: String): HomeTemplate {
        this.entries = entries
        val homeTemplateEntry = getEntryById(homeTemplateId)

        return if (homeTemplateEntry.entryType == EntryType.GUEST_HOME_TEMPLATE)
            getGuestHomeTemplate(homeTemplateEntry.entryData)
        else
            getResidentHomeTemplate(homeTemplateEntry.entryData)
    }

    private fun getGuestHomeTemplate(data: JSONObject): GuestHomeTemplate {
        val guestHomeTemplateData = cmpJsonDataHelper.mapToGuestHomeTemplateData(data)
        val leftColumn = getSwimLaneById(guestHomeTemplateData.widgetColumn1)
        val centerColumn = getSwimLaneById(guestHomeTemplateData.widgetColumn2)
        val rightColumn = getSwimLaneById(guestHomeTemplateData.widgetColumn3)

        return GuestHomeTemplate(leftColumn, centerColumn, rightColumn)
    }

    private fun getResidentHomeTemplate(data: JSONObject): ResidentHomeTemplate {
        val residentHomeTemplateData = cmpJsonDataHelper.mapToResidentHomeTemplateData(data)
        val contentContainer = residentHomeTemplateData.contentContainers.map { swimLaneId ->
            getSwimLaneById(swimLaneId)
        }

        val heroCarouselItems = residentHomeTemplateData.heroCarouselItems.mapNotNull { widgetId ->
            getWidgetById(widgetId)
        }

        return ResidentHomeTemplate(
            contentContainers = contentContainer,
            title = residentHomeTemplateData.title,
            heroCarouselItems = heroCarouselItems
        )
    }

    private fun getSwimLaneById(swimLaneId: String): SwimLaneContainer {
        val swimLaneEntry = getEntryById(swimLaneId)
        return if (swimLaneEntry.entryType == EntryType.SWIMLANE_WIDGET_LARGE)
            getLargeSwimLane(swimLaneEntry.entryData)
        else
            getSwimLane(swimLaneEntry.entryData)
    }

    private fun getSwimLane(data: JSONObject): SwimLaneContainer {
        val swimLaneData = cmpJsonDataHelper.mapToSwimLaneWidgetContainerData(data)
        val widgets = swimLaneData.widgetContents.mapNotNull { widgetId -> getWidgetById(widgetId) }
        return SwimLaneContainer(
            widgets = widgets,
            title = swimLaneData.title,
            type = SwimLaneType.NORMAL
        )
    }

    private fun getLargeSwimLane(data: JSONObject): SwimLaneContainer {
        val swimLaneLargeData = cmpJsonDataHelper.mapToSwimLaneWidgetLargeContainerData(data)
        val widgets = swimLaneLargeData.widgetContents.mapNotNull { widgetId -> getWidgetById(widgetId) }
        return SwimLaneContainer(
            widgets = widgets,
            title = swimLaneLargeData.title,
            type = SwimLaneType.LARGE
        )
    }

    private fun getWidgetById(widgetId: String): Widget? {
        val widgetEntry = getEntryById(widgetId)
        return when (widgetEntry.entryType) {
            EntryType.LIVE_CHANNEL_WIDGET -> getLiveChannelWidget(widgetEntry.entryData)
            EntryType.MORE_INFO_WIDGET -> getMoreInfoWidget(widgetEntry.entryData)
            EntryType.NOTIFICATION_WIDGET -> getNotificationWidget(widgetEntry.entryData)
            EntryType.SPORTS_WIDGET -> getSportsWidget(widgetEntry.entryData)
            EntryType.STATIC_AD_WIDGET -> getStaticAdWidget(widgetEntry.entryData)
            EntryType.WEATHER_WIDGET -> getWeatherWidget(widgetEntry.entryData)
            else -> null
        }
    }

    private fun getLiveChannelWidget(data: JSONObject): LiveChannelWidget {
        val liveChannelWidgetData = cmpJsonDataHelper.mapToLiveChannelWidgetData(data)
        return LiveChannelWidget(title = liveChannelWidgetData.title, channelId = liveChannelWidgetData.channelId)
    }

    private fun getMoreInfoWidget(data: JSONObject): MoreInfoWidget {
        val moreInfoWidgetData = cmpJsonDataHelper.mapToMoreInfoWidgetData(data)
        return MoreInfoWidget(
            title = moreInfoWidgetData.title,
            subTitle = moreInfoWidgetData.subTitle,
            headerImage = moreInfoWidgetData.headerImage.toImage(),
            contents = getMoreInfoContents(moreInfoWidgetData.contents),
            name = moreInfoWidgetData.name,
            icon = moreInfoWidgetData.icon.toImage(),
            link = moreInfoWidgetData.link,
            contentTitle = moreInfoWidgetData.contentTitle
        )
    }

    private fun getNotificationWidget(data: JSONObject): NotificationWidget {
        val notificationWidgetData = cmpJsonDataHelper.mapToNotificationWidgetData(data)
        return NotificationWidget(
            icon = notificationWidgetData.icon.toImage(),
            link = notificationWidgetData.link,
            title = notificationWidgetData.title,
            description = notificationWidgetData.description
        )
    }

    private fun getSportsWidget(data: JSONObject): SportsWidget {
        val sportsWidgetData = cmpJsonDataHelper.mapToSportsWidgetData(data)
        return SportsWidget(title = sportsWidgetData.title)
    }

    private fun getStaticAdWidget(data: JSONObject): StaticAdWidget {
        val staticAdWidgetData = cmpJsonDataHelper.mapToStaticAdWidgetData(data)
        return StaticAdWidget(
            adImage = staticAdWidgetData.adImage.toImage(),
            subTitle = staticAdWidgetData.subTitle,
            name = staticAdWidgetData.name,
            icon = staticAdWidgetData.icon.toImage(),
            link = staticAdWidgetData.link,
            title = staticAdWidgetData.title,
            description = staticAdWidgetData.description
        )
    }

    private fun getWeatherWidget(data: JSONObject): WeatherWidget {
        val weatherWidgetData = cmpJsonDataHelper.mapToWeatherWidgetData(data)
        return WeatherWidget(title = weatherWidgetData.title)
    }

    private fun getMoreInfoContents(contents: List<String>): List<MoreInfoContent> {
        return contents.map { getMoreInfoContent(getEntryById(it).entryData) }
    }

    private fun getMoreInfoContent(data: JSONObject): MoreInfoContent {
        val moreInfoContentData = cmpJsonDataHelper.mapToMoreInfoContentData(data)
        return MoreInfoContent(
            header = moreInfoContentData.header,
            paragraph = moreInfoContentData.paragraph,
            image = moreInfoContentData.image?.toImage() ?: Image("")
        )
    }

    private fun getEntryById(id: String): EntryEntity {
        return this.entries.getValue(id)
    }
}