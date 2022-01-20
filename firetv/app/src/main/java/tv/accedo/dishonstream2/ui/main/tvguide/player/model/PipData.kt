package tv.accedo.dishonstream2.ui.main.tvguide.player.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import tv.accedo.dishonstream2.domain.model.supair.Channel
import tv.accedo.dishonstream2.domain.model.supair.Program

@Parcelize
class PipData(
    val channel: Channel,
    val program: Program
) : Parcelable