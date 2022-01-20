package tv.accedo.dishonstream2.data.datahelper

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import tv.accedo.dishonstream2.data.db.model.ChannelEntity
import tv.accedo.dishonstream2.data.model.smartbox.ProgramData
import tv.accedo.dishonstream2.data.model.smartbox.ServiceData
import tv.accedo.dishonstream2.data.model.supair.ChannelInfoData
import tv.accedo.dishonstream2.domain.model.supair.Channel
import tv.accedo.dishonstream2.domain.model.supair.ChannelInfo
import tv.accedo.dishonstream2.domain.model.supair.Program

/**
 * Class responsible for mapping epg related data models to domain entities and vice versa
 */
class EpgDataHelper {

    fun mapServiceDataAndChannelInfoDataToChannelEntity(
        serviceData: ServiceData,
        channelInfoData: ChannelInfoData
    ): ChannelEntity {
        return ChannelEntity(
            id = channelInfoData.suId,
            callSign = channelInfoData.callSign,
            serviceName = serviceData.serviceName,
            contentId = serviceData.contentID,
            isHD = channelInfoData.isHD,
            serviceType = channelInfoData.serviceType,
            serviceId = channelInfoData.serviceId,
            logoURL = channelInfoData.logoURL
        )
    }

    fun fromChannelEntityMapToChannelMap(serviceDataToChannelMap: Map<ServiceData, ChannelEntity?>): Map<ServiceData, Channel> {
        var minStartTime = Clock.System.now().toEpochMilliseconds()
        cleanProgramData(serviceDataToChannelMap.keys.map { it.events }.flatten()).forEach {
            minStartTime = minOf(minStartTime, Instant.parse(it.startTime).toEpochMilliseconds())
        }

        return serviceDataToChannelMap.map { (serviceData, channel) ->
            serviceData to Channel(
                name = channel?.serviceName ?: serviceData.serviceName,
                contentId = serviceData.contentID,
                serviceKey = serviceData.serviceKey,
                URLDASH = serviceData.serviceUrlDASH,
                URLHLS = serviceData.serviceUrlHLS,
                programs = mapProgramDataListToProgramList(serviceData.events, minStartTime),
                detail = if (channel == null) null else mapChannelToChannelInfo(channel)
            )
        }.toMap()
    }

    private fun mapChannelToChannelInfo(channel: ChannelEntity): ChannelInfo {
        return ChannelInfo(
            id = channel.id,
            callSign = channel.callSign,
            isHD = channel.isHD,
            serviceId = channel.serviceId,
            serviceType = channel.serviceType,
            logoURL = channel.logoURL.replace("http", "https")
        )
    }

    private fun mapProgramDataListToProgramList(
        programDataList: List<ProgramData>,
        minProgramTime: Long
    ): List<Program> {
        return cleanProgramData(programDataList).map { programData ->
            val startTime = Instant.parse(programData.startTime).toEpochMilliseconds()
            val endTime = startTime + (programData.duration * 1000)
            Program(
                name = programData.eventName,
                startTime = startTime,
                endTime = endTime,
                duration = programData.duration.toLong(),
                echoStarId = programData.echoStarId,
                startTimeFake = minProgramTime
            )
        }
    }

    private fun cleanProgramData(events: List<ProgramData>): List<ProgramData> {
        return events.filter { e ->
            val currentTime = Clock.System.now().toEpochMilliseconds()
            val startTime = Instant.parse(e.startTime).toEpochMilliseconds()
            val endTime = startTime + (e.duration * 1000)
            return@filter endTime >= currentTime
        }
    }

    fun cleanPrograms(programs: List<Program>): List<Program> {
        return programs.filter { e ->
            val currentTime = Clock.System.now().toEpochMilliseconds()
            val endTime = e.startTime + (e.duration * 1000)
            return@filter endTime >= currentTime
        }
    }
}