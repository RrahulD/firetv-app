package tv.accedo.dishonstream2.domain.model.settings

data class FireTVDevice(
    val name:String,
    val buildModel:String,
    val androidLevel:String,
    val fireOSVersion:String
)