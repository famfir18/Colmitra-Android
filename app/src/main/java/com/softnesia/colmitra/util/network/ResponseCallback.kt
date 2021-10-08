package com.softnesia.colmitra.util.network

interface ResponseCallback<T> {
    fun onSuccess(data: T)
    fun onFailed(message: String?)
    fun onEmpty(message: String?)
}