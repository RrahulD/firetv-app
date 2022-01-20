package tv.accedo.dishonstream2.domain.model.home.template

import org.json.JSONObject

data class EntryEntity(
    val entryType: EntryType,
    val id: String,
    val entryData: JSONObject
)

enum class EntryType(
    val type: String
) {
    // Home template entry types
    GUEST_HOME_TEMPLATE("home-template-guests--fire-tv"),
    RESIDENT_HOME_TEMPLATE("home-template-resident"),

    // swimlane entry types
    SWIMLANE_WIDGET_LARGE("swimlane--widgets-large"),
    SWIMLANE_WIDGET("swimlane--widgets"),

    // widget entry types
    LIVE_CHANNEL_WIDGET("widget--live-channel"),
    MORE_INFO_WIDGET("widget--more-info"),
    NOTIFICATION_WIDGET("widget--notification"),
    SPORTS_WIDGET("widget--sports"),
    STATIC_AD_WIDGET("widget--static-ad"),
    WEATHER_WIDGET("widget--weather"),

    // other
    MORE_INFO_CONTENT("more-info-content"),

    // FAQ
    FAQ("faq-firetv"),
    FAQ_SECTION("faq-content-section-firetv"),
    FAQ_CONTENT("faq-content--fire-tv")
}