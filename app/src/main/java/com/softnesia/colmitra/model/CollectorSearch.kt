package com.softnesia.colmitra.model

import com.softnesia.colmitra.service.api.ApiService
import com.softnesia.colmitra.service.api.DefaultCallback
import com.softnesia.colmitra.util.network.ResponseCallback
import retrofit2.Call

object CollectorSearch {
    fun detail(search: String, callback: ResponseCallback<Collector>): Call<*> {
        val call = ApiService.client.getCollectorSearch(search)

        call.enqueue(object : DefaultCallback<Collector>() {
            override fun onSuccess(response: Collector?, code: Int) {
                response?.also {
                    if (!it.success) onError(it.message)
                    else callback.onSuccess(it)
                } ?: onError("Gagal")
            }

            override fun onError(message: String?) {
                callback.onFailed(message)
            }
        })
        return call
    }
}