package com.example.optitrip.utils

/**
 * Wrapper class for an APi call result.
 * It contains a status, data and or erro message
 *
 * @param T the type of the call result if successful
 * @property status the current status
 * @property data the result of the succesful call
 * @property apiError the error message
 */
class Resource<T> private constructor(val status: Resource.Status, val data: T?, val apiError:String?) {
    enum class Status {
        SUCCESS, ERROR, LOADING
    }
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }
        fun <T> error(apiError: String?): Resource<T> {
            return Resource(Status.ERROR, null, apiError)
        }
        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }
}