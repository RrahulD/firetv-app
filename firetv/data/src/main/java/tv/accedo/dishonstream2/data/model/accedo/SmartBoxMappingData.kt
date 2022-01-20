package tv.accedo.dishonstream2.data.model.accedo

data class SmartBoxMappingData(
    val mappings: Map<String, SmartBoxMapping>
)

data class SmartBoxMapping(
    val appGroupName: String,
    val UUID: String,
    val appKey: String
)