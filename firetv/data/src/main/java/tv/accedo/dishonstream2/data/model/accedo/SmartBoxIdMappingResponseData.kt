package tv.accedo.dishonstream2.data.model.accedo

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

data class SmartBoxIdMappingResponseData(
    @SerializedName("smartboxidmappings")
    val smartBoxIdMappings: MappingsObjectWrapper
) {
    fun toSmartBoxMappingData(): SmartBoxMappingData {
        val smartBoxMappings: Map<String, SmartBoxMapping> = Gson().fromJson(
            smartBoxIdMappings.mappingsObject,
            object : TypeToken<Map<String, SmartBoxMapping>>() {}.type
        )
        return SmartBoxMappingData(mappings = smartBoxMappings)
    }
}

data class MappingsObjectWrapper(
    val mappingsObject: String
)

