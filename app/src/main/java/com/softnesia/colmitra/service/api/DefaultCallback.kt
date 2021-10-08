package com.softnesia.colmitra.service.api

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InterruptedIOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

abstract class DefaultCallback<T> : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            response.body()?.let {
                onSuccess(response.body(), response.code())
                return
            }
        }

        val error = RetrofitHelper.parseError(response)
        onError(error.message)
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        var message = t.message
        if (t is SocketTimeoutException || t is TimeoutException) {
            message = "Whoops, server is busy, please try again."
        } else if (t is InterruptedIOException) {
            message = "Whoops, the connection is interrupted, please check your internet service."
        }
        onError(message)
    }

    abstract fun onSuccess(response: T?, code: Int)
    abstract fun onError(message: String?)
}