package tv.accedo.dishonstream2.domain.usecase.home

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import tv.accedo.dishonstream2.domain.repository.CMPRepository

enum class Tab {
    HOME, TV_GUIDE, SETTINGS, SPORTS, SEARCH, ON_DEMAND, NONE
}

class GetHomePageTabsUseCase(
    private val cmpRepository: CMPRepository
) {
    suspend operator fun invoke(): List<Tab> = withContext(Dispatchers.IO) {
        val tabs = mutableListOf<Tab>()
        val homeTabOptions = JSONObject(cmpRepository.getMetadata(MAIN_MENU_OPTIONS_KEY))
        if (getBoolean(homeTabOptions, HOME_KEY))
            tabs.add(Tab.HOME)

        if (homeTabOptions.getBoolean(TV_GUIDE_KEY))
            tabs.add(Tab.TV_GUIDE)

        if (getBoolean(homeTabOptions, SETTINGS_KEY))
            tabs.add(Tab.SETTINGS)

        if (getBoolean(homeTabOptions, SEARCH_KEY))
            tabs.add(Tab.SEARCH)

        if (getBoolean(homeTabOptions, ON_DEMAND_KEY))
            tabs.add(Tab.ON_DEMAND)

        /*if (getBoolean(homeTabOptions, RECORDINGS_KEY))
            tabs.add(RECORDINGS_KEY)*/

        tabs
    }

    private fun getBoolean(jsonObject: JSONObject, key: String): Boolean {
        return try {
            jsonObject.getBoolean(key)
        } catch (ex: Exception) {
            false
        }
    }

    companion object {
        private const val MAIN_MENU_OPTIONS_KEY = "navigationMenuFireTv"
        private const val HOME_KEY = "home"
        private const val TV_GUIDE_KEY = "tvGuide"
        private const val ON_DEMAND_KEY = "onDemand"
        private const val RECORDINGS_KEY = "recordings"
        private const val SEARCH_KEY = "search"
        private const val SETTINGS_KEY = "settings"
    }
}