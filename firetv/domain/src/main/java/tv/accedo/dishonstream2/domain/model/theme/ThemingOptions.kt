package tv.accedo.dishonstream2.domain.model.theme

data class ThemingOptions(
    val guestPrimaryColor: String,
    val residentPrimaryColor: String,
    val residentSecondaryColor: String,
    val backgroundImage: String,
    val defaultButtonText: String,
    val defaultButtonBackground: String,
    val liveColor: String,
    val headerTextDefault: String,
    val headerTextActive: String,
    val modalPopUpHeading: String,
    val modalPopUpSubHeading: String,
    val modalPopUpText: String,
    val modalPopUpBackground: String,
    val modalPopUpBackgroundFullScreen: String,
    val modalButtonText: String,
    val widgetHeading: String,
    val widgetSubHeading: String,
    val widgetText: String,
    val swimlaneTitleText: String,
    val widgetBackground: String,
    val playerDefaultText: String,
    val playerLightText: String,
    val overlayBackground: String,
    val overlayOpacity: Double,
    val useLightTheme: Boolean
)