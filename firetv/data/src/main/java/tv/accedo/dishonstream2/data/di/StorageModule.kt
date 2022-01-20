package tv.accedo.dishonstream2.data.di

import org.koin.dsl.module
import tv.accedo.dishonstream2.data.db.DishDatabase
import tv.accedo.dishonstream2.data.repository.caching.CacheStore
import tv.accedo.dishonstream2.data.repository.caching.InMemoryCacheStore
import tv.accedo.dishonstream2.data.storage.KeyValueStoreImpl
import tv.accedo.dishonstream2.domain.storage.KeyValueStore

val storageModule = module {

    fun provideChannelDao(dishDatabase: DishDatabase) = dishDatabase.channelDao()

    single { DishDatabase.getDatabase(get()) }
    single { provideChannelDao(get()) }
    single<CacheStore> { InMemoryCacheStore() }
    single<KeyValueStore> { KeyValueStoreImpl(get()) }
}