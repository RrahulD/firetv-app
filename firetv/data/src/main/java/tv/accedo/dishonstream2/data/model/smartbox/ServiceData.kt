package tv.accedo.dishonstream2.data.model.smartbox

data class ServiceData(
    val serviceName: String,
    val contentID: String,
    val serviceKey: Long,
    val serviceUrlHLS: String,
    val serviceUrlDASH: String,
    val numberOfEvents: Int,
    val events: List<ProgramData>
)