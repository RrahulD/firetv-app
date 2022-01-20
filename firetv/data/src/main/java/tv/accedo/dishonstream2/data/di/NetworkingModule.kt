package tv.accedo.dishonstream2.data.di

import org.koin.dsl.module
import tv.accedo.dishonstream2.data.networking.client.DishRetrofitClient

val networkingModule = module {
    factory { DishRetrofitClient() }
}