package tv.accedo.dishonstream2.extensions

import java.util.*

fun String.capitalise() =
    this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
