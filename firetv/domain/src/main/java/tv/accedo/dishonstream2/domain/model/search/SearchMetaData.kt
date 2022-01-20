package tv.accedo.dishonstream2.domain.model.search

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import tv.accedo.dishonstream2.domain.model.supair.Program
import tv.accedo.dishonstream2.domain.model.supair.ProgramInfo

@Parcelize
data class SearchEpgProgramData(val program: Program, val programInfo: ProgramInfo?) : Parcelable

data class SearchResultsData(val title: String, val data: List<Any>, val total: Int? = 0, val searchText: String? = null)
