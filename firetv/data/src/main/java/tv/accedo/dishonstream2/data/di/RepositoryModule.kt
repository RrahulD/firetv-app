package tv.accedo.dishonstream2.data.di


import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import tv.accedo.dishonstream2.data.repository.A1ControlRepositoryV2Impl
import tv.accedo.dishonstream2.data.repository.DishRepositoryImpl
import tv.accedo.dishonstream2.data.repository.DishSmartBoxRepositoryImpl
import tv.accedo.dishonstream2.data.repository.ReelGoodVODRepository
import tv.accedo.dishonstream2.domain.repository.CMPRepository
import tv.accedo.dishonstream2.domain.repository.DishRepository
import tv.accedo.dishonstream2.domain.repository.DishSmartBoxRepository
import tv.accedo.dishonstream2.domain.repository.VODRepository

val repositoryModule = module {
    single<DishRepository> { DishRepositoryImpl(get(), get(), get(), get(), get()) }
    single<CMPRepository> { A1ControlRepositoryV2Impl(androidContext(), get(), get(named("device_id"))) }
    single<DishSmartBoxRepository> { DishSmartBoxRepositoryImpl(get()) }
    single<VODRepository> { ReelGoodVODRepository(get()) }
}