package tv.accedo.dishonstream2.data.repository

import tv.accedo.dishonstream2.data.networking.client.DishRetrofitClient
import tv.accedo.dishonstream2.domain.repository.DishSmartBoxRepository

class DishSmartBoxRepositoryImpl(
    private val dishRetrofitClient: DishRetrofitClient
) : DishSmartBoxRepository {

    override suspend fun getSmartBoxIdentifier(): String {
        val disSmartBoxApiService = dishRetrofitClient.getDishSmartboxApiService("https://streaming0.watchdishtv.com")
        return disSmartBoxApiService.getIdentifier().smartboxIdentifier
    }
}