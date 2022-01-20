package tv.accedo.dishonstream2.ui.main.tvguide.player.model

data class PlaybackInfo(
    val playerLicense: String,
    val drmLicenseURL: String,
    val drmToken: String,
    val hlsURL: String,
    val dashURL: String,
    val startTime: Long,
    val endTime: Long
)