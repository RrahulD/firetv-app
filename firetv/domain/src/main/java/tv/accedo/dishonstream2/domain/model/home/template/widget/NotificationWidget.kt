package tv.accedo.dishonstream2.domain.model.home.template.widget

import tv.accedo.dishonstream2.domain.model.home.template.base.Image

data class NotificationWidget(
    val icon: Image,
    val link: String,
    val title: String,
    val description: String
) : Widget