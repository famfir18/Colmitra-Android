package com.softnesia.colmitra.model

import com.softnesia.colmitra.service.api.ApiService.client
import com.softnesia.colmitra.service.api.DefaultCallback
import com.softnesia.colmitra.util.network.ResponseCallback
import retrofit2.Call

/**
 * Created by Dark on 21/01/2018.
 */
object CollectorRepository {
    fun detail(page: Int, callback: ResponseCallback<Collector>): Call<*> {
        val call = client.getCollector(page)

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