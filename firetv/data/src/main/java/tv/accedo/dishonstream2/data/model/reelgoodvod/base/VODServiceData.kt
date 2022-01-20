package tv.accedo.dishonstream2.data.model.reelgoodvod.base


import com.google.gson.annotations.SerializedName

data class VODServiceResults(
    val result: List<VODServiceData>
)

data class VODServiceData(
    val id: Int,
    val name: String,
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("display_index")
    val displayIndex: Int,
    @SerializedName("priority_index")
    val priorityIndex: Int
)