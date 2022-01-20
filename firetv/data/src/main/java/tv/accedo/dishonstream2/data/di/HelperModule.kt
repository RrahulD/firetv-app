package tv.accedo.dishonstream2.data.di

import hu.accedo.commons.tools.DeviceIdentifier
import org.koin.core.qualifier.named
import org.koin.dsl.module
import tv.accedo.dishonstream2.data.datahelper.*
import tv.accedo.dishonstream2.domain.datahelper.FAQHelper
import tv.accedo.dishonstream2.domain.datahelper.TemplateHelper

val helperModule = module {
    factory { EpgDataHelper() }
    factory { CMPJsonDataHelper() }
    factory<FAQHelper> { FAQHelperImpl(get()) }
    factory<TemplateHelper> { TemplateHelperImpl(get()) }
    factory { SportsDataHelper() }
    single(named("device_id")) { DeviceIdentifier.getDeviceId(get()) }
}