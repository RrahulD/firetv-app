package tv.accedo.dishonstream2.ui.base.resource

sealed class Resource<T> {
    class Loading<T> : Resource<T>()
    class Success<T>(val data: T) : Resource<T>()
    class Failure<T>(val error: Throwable) : Resource<T>()
}