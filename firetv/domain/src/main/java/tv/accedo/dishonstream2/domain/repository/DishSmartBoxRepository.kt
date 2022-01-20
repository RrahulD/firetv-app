package tv.accedo.dishonstream2.domain.repository

interface DishSmartBoxRepository {
    suspend fun getSmartBoxIdentifier(): String
}