package tv.accedo.dishonstream2.ui.theme

import android.content.Context
import android.graphics.Color
import android.graphics.RectF
import android.graphics.drawable.*
import android.graphics.drawable.shapes.RoundRectShape
import androidx.core.content.ContextCompat
import timber.log.Timber
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.domain.model.theme.ThemingOptions
import tv.accedo.dishonstream2.domain.usecase.home.HomeTemplateType


class ThemeManager(
    private val context: Context
) {
    private var themingOptions: ThemingOptions? = null
    private var templateType: HomeTemplateType = HomeTemplateType.RESIDENT

    val usLightTheme
        get() = themingOptions?.useLightTheme ?: false

    fun initialize(themingOptions: ThemingOptions, templateType: HomeTemplateType) {
        this.themingOptions = themingOptions
        this.templateType = templateType
    }

    fun getWidgetBackgroundDrawable(): Drawable? {
        return themingOptions?.let {
            try {
                StateListDrawable().apply {
                    addState(
                        intArrayOf(android.R.attr.state_focused),
                        ColorDrawable(getPrimaryColorInternal(it))
                    )

                    addState(
                        intArrayOf(),
                        ColorDrawable(parseColor(it.widgetBackground))
                    )
                }
            } catch (ex: Exception) {
                Timber.e("Exception while getting the widget background from theme manager")
                Timber.e(ex)
                null
            }
        }
    }

    fun getPrimaryButtonBackgroundDrawable(): Drawable? {
        return themingOptions?.let {
            try {
                StateListDrawable().apply {
                    addState(
                        intArrayOf(android.R.attr.state_focused),
                        ContextCompat.getDrawable(context, R.drawable.primary_button_selected).apply {
                            if (this is GradientDrawable) this.setColor(getPrimaryColorInternal(it))
                        }
                    )

                    addState(
                        intArrayOf(),
                        ContextCompat.getDrawable(context, R.drawable.primary_button_unselected).apply {
                            if (this is GradientDrawable) this.setColor(parseColor(it.defaultButtonBackground))
                        }
                    )
                }
            } catch (ex: Exception) {
                Timber.e("Exception while getting the primary button background from theme manager")
                Timber.e(ex)
                null
            }
        }
    }

    fun getStaticAdRoundedBackgroundDrawable(): Drawable? {
        return themingOptions?.let {
            try {
                StateListDrawable().apply {
                    addState(
                        intArrayOf(android.R.attr.state_focused),
                        ShapeDrawable(
                            RoundRectShape(
                                floatArrayOf(20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f),
                                RectF(10f, 10f, 10f, 10f),
                                floatArrayOf(20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f)
                            )
                        ).apply {
                            paint.color = getPrimaryColorInternal(it)
                        }
                    )
                }
            } catch (ex: Exception) {
                Timber.e("Exception while getting the static ad rounded background drawable from theme manager")
                Timber.e(ex)
                null
            }
        }
    }

    fun getEpgItemBackgroundDrawable(): Drawable? {
        return themingOptions?.let {
            try {

                StateListDrawable().apply {
                    addState(
                        intArrayOf(android.R.attr.state_focused),
                        ContextCompat.getDrawable(context, R.drawable.epg_program_selected_background).apply {
                            if (this is GradientDrawable) this.setColor(getPrimaryColorInternal(it))
                        }
                    )

                    addState(
                        intArrayOf(),
                        ContextCompat.getDrawable(context, R.drawable.epg_program_unselected_background)
                    )
                }
            } catch (ex: Exception) {
                Timber.e("Exception while getting the epg item background from theme manager")
                Timber.e(ex)
                null
            }
        }
    }

    fun getPlayerControlBackgroundDrawable(): Drawable? {
        return themingOptions?.let {
            try {

                StateListDrawable().apply {
                    addState(
                        intArrayOf(android.R.attr.state_focused),
                        ContextCompat.getDrawable(context, R.drawable.player_control_selected).apply {
                            if (this is GradientDrawable) this.setColor(getPrimaryColorInternal(it))
                        }
                    )

                    addState(
                        intArrayOf(),
                        ContextCompat.getDrawable(context, R.drawable.player_control_unselected)
                    )
                }
            } catch (ex: Exception) {
                Timber.e("Exception while getting player control background from theme manager")
                Timber.e(ex)
                null
            }
        }
    }

    fun getSettingOptionsBackgroundDrawable(): Drawable? {
        return themingOptions?.let {
            try {
                StateListDrawable().apply {
                    addState(
                        intArrayOf(android.R.attr.state_focused),
                        ContextCompat.getDrawable(context, R.drawable.settings_option_selected_background).apply {
                            if (this is GradientDrawable) this.setColor(getPrimaryColorInternal(it))
                        }
                    )
                    addState(
                        intArrayOf(),
                        ContextCompat.getDrawable(context, R.drawable.settings_option_unselected_background)
                    )
                }
            } catch (ex: Exception) {
                Timber.e("Exception while getting the settings option background from theme manager")
                Timber.e(ex)
                null
            }
        }
    }

    fun getSettingsSubOptionsBackgroundDrawable(): Drawable? {
        return themingOptions?.let {
            try {
                StateListDrawable().apply {
                    addState(
                        intArrayOf(android.R.attr.state_focused),
                        ContextCompat.getDrawable(context, R.drawable.settings_sub_option_selected_background).apply {
                            if (this is GradientDrawable) this.setColor(getPrimaryColorInternal(it))
                        }
                    )
                    addState(
                        intArrayOf(),
                        ContextCompat.getDrawable(context, R.drawable.settings_sub_option_unselected_background).apply {
                            if (this is GradientDrawable) this.setColor(parseColor(it.widgetBackground))
                        }
                    )
                }
            } catch (ex: Exception) {
                Timber.e("Exception while getting the settings sub options background from theme manager")
                Timber.e(ex)
                null
            }
        }
    }

    fun getPrimaryColor(): Int? {
        return themingOptions?.let {
            try {
                getPrimaryColorInternal(it)
            } catch (ex: Exception) {
                Timber.e("Exception while getting the primary color from theme manager")
                Timber.e(ex)
                null
            }
        }
    }

    private fun getPrimaryColorInternal(themingOptions: ThemingOptions): Int {
        return parseColor(
            if (templateType == HomeTemplateType.RESIDENT)
                themingOptions.residentPrimaryColor else themingOptions.guestPrimaryColor
        )
    }

    private fun parseColor(colorString: String): Int {
        if (colorString.contains("rgb", true)) {
            val values = colorString.substring(colorString.indexOf("(") + 1, colorString.indexOf(")"))
            val colorValues = values.split(",").map { it.trim() }
            val alpha = (colorValues[3].toFloat() * 255).toInt()
            return Color.argb(alpha, colorValues[0].toInt(), colorValues[1].toInt(), colorValues[2].toInt())
        }
        return Color.parseColor(colorString)
    }
}