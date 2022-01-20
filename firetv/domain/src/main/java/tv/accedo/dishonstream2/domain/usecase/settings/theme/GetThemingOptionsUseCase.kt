package tv.accedo.dishonstream2.domain.usecase.settings.theme

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import tv.accedo.dishonstream2.domain.model.theme.ThemingOptions
import tv.accedo.dishonstream2.domain.repository.CMPRepository

class GetThemingOptionsUseCase(
    private val cmpRepository: CMPRepository
) {
    suspend operator fun invoke(): ThemingOptions = withContext(Dispatchers.IO) {
        val themingOptions = JSONObject(cmpRepository.getMetadata(THEMING_OPTIONS_KEY))
        val useLightTheme = themingOptions.getBoolean(USE_LIGHT_THEME_KEY)
        val defaultTheme = themingOptions.getJSONObject(DEFAULT_THEME)
        val lightTheme = themingOptions.getJSONObject(LIGHT_THEME)
        ThemingOptions(
            guestPrimaryColor = getColor(useLightTheme, defaultTheme, lightTheme, GUEST_PRIMARY_COLOR, "#D9A223"),
            residentPrimaryColor = getColor(useLightTheme, defaultTheme, lightTheme, RESIDENT_PRIMARY_COLOR, "#385DF7"),
            residentSecondaryColor = getColor(useLightTheme, defaultTheme, lightTheme, RESIDENT_SECONDARY_COLOR, "#14111E"),
            backgroundImage = "",
            defaultButtonText = getColor(useLightTheme, defaultTheme, lightTheme, DEFAULT_BUTTON_TEXT, "#ffffff"),
            defaultButtonBackground = getColor(
                useLightTheme,
                defaultTheme,
                lightTheme,
                DEFAULT_BUTTON_BACKGROUND,
                "rgba(255, 255, 255, 0.2)"
            ),
            liveColor = getColor(useLightTheme, defaultTheme, lightTheme, LIVE_COLOR, "#ff1616"),
            headerTextDefault = getColor(useLightTheme, defaultTheme, lightTheme, HEADER_TEXT_DEFAULT, "#dddddd"),
            headerTextActive = getColor(useLightTheme, defaultTheme, lightTheme, HEADER_TEXT_ACTIVE, "#ffffff"),
            modalPopUpHeading = getColor(useLightTheme, defaultTheme, lightTheme, MODAL_POP_UP_HEADING, "#f8f8f8"),
            modalPopUpSubHeading = getColor(useLightTheme, defaultTheme, lightTheme, MODAL_POP_UP_SUB_HEADING, "#aaaaaa"),
            modalButtonText = getColor(useLightTheme, defaultTheme, lightTheme, MODAL_BUTTON_TEXT, "#f8f8f8"),
            modalPopUpText = getColor(useLightTheme, defaultTheme, lightTheme, MODAL_POP_UP_TEXT, "#dddddd"),
            modalPopUpBackground = getColor(useLightTheme, defaultTheme, lightTheme, MODAL_POP_UP_BACKGROUND, "#191919"),
            modalPopUpBackgroundFullScreen = getColor(
                useLightTheme,
                defaultTheme,
                lightTheme,
                MODAL_POP_UP_BACKGROUND_FULL_SCREEN,
                "#050505"
            ),
            widgetHeading = getColor(useLightTheme, defaultTheme, lightTheme, WIDGET_HEADING, "#ffffff"),
            widgetSubHeading = getColor(useLightTheme, defaultTheme, lightTheme, WIDGET_SUB_HEADING, "#f8f8f8"),
            widgetText = getColor(useLightTheme, defaultTheme, lightTheme, WIDGET_TEXT, "#ffffff"),
            swimlaneTitleText = getColor(useLightTheme, defaultTheme, lightTheme, SWIMLANE_TITLE_TEXT, "#ffffff"),
            widgetBackground = getColor(useLightTheme, defaultTheme, lightTheme, WIDGET_BACKGROUND, "#191919"),
            playerDefaultText = getColor(useLightTheme, defaultTheme, lightTheme, PLAYER_DEFAULT_TEXT, "#f8f8f8"),
            playerLightText = getColor(useLightTheme, defaultTheme, lightTheme, PLAYER_LIGHT_TEXT, "#dddddd"),
            overlayBackground = getColor(useLightTheme, defaultTheme, lightTheme, OVERLAY_BACKGROUND, "#050505"),
            overlayOpacity = 0.5,
            useLightTheme = useLightTheme
        )
    }

    private fun getColor(
        useLightTheme: Boolean,
        defaultTheme: JSONObject,
        lightTheme: JSONObject,
        key: String,
        defaultColor: String
    ): String {
        return if (useLightTheme && lightTheme.has(key))
            lightTheme.getString(key)
        else if (defaultTheme.has(key))
            defaultTheme.getString(key)
        else defaultColor
    }

    companion object {
        private const val THEMING_OPTIONS_KEY = "themingOptions"
        private const val USE_LIGHT_THEME_KEY = "useLightTheme"
        private const val DEFAULT_THEME = "defaultTheme"
        private const val LIGHT_THEME = "lightTheme"
        private const val GUEST_PRIMARY_COLOR = "guestPrimaryColor"
        private const val RESIDENT_PRIMARY_COLOR = "residentPrimaryColor"
        private const val RESIDENT_SECONDARY_COLOR = "residentSecondaryColor"
        private const val BACKGROUND_IMAGE = "backgroundImage"
        private const val DEFAULT_BUTTON_TEXT = "defaultButtonText"
        private const val DEFAULT_BUTTON_BACKGROUND = "defaultButtonBackground"
        private const val LIVE_COLOR = "liveColor"
        private const val HEADER_TEXT_DEFAULT = "headerTextDefault"
        private const val HEADER_TEXT_ACTIVE = "headerTextActive"
        private const val MODAL_POP_UP_HEADING = "modalPopUpHeading"
        private const val MODAL_POP_UP_SUB_HEADING = "modalPopUpSubHeading"
        private const val MODAL_POP_UP_TEXT = "modalPopUpText"
        private const val MODAL_POP_UP_BACKGROUND = "modalPopUpBackground"
        private const val MODAL_POP_UP_BACKGROUND_FULL_SCREEN = "modalPopUpBackgroundFullScreen"
        private const val MODAL_BUTTON_TEXT = "modalButtonText"
        private const val WIDGET_HEADING = "widgetHeading"
        private const val WIDGET_SUB_HEADING = "widgetSubHeading"
        private const val WIDGET_TEXT = "widgetText"
        private const val SWIMLANE_TITLE_TEXT = "swimlaneTitleText"
        private const val WIDGET_BACKGROUND = "widgetBackground"
        private const val PLAYER_DEFAULT_TEXT = "playerDefaultText"
        private const val PLAYER_LIGHT_TEXT = "playerLightText"
        private const val OVERLAY_BACKGROUND = "overlayBackground"
        private const val OVERLAY_OPACITY = "overlayOpacity"
        private const val PRIMARY_COLOR = "primaryColor"
    }
}