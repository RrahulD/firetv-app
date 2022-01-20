package tv.accedo.dishonstream2.extensions

import android.graphics.drawable.Drawable

fun Drawable.copy(): Drawable? = this.constantState?.newDrawable()?.mutate()
