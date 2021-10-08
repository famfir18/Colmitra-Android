package com.softnesia.colmitra.model.customer

import com.softnesia.colmitra.service.api.ApiResponse
import com.softnesia.colmitra.service.api.ApiService.client
import com.softnesia.colmitra.service.api.DefaultCallback
import com.softnesia.colmitra.util.network.ResponseCallback
import retrofit2.Call

/**
 * Created by Dark on 21/01/2018.
 */
object CustomerRepository {
    fun detail(id: Long, callback: ResponseCallback<CustomerResponse>): Call<*> {
        val call = client.customerDetail(id)

        call.enqueue(object : DefaultCallback<CustomerResponse>() {
            override fun onSuccess(response: CustomerResponse?, code: Int) {
                callback.onSuccess(response!!)
            }

            override fun onError(message: String?) {
                callback.onFailed(message)
            }
        })
        return call
    }

    fun updateStatus(
        body: HashMap<String, Any>,
        callback: ResponseCallback<String?>
    ): Call<*> {
        val call = client.updateCustomerStatus(body)

        call.enqueue(object : DefaultCallback<ApiResponse<Any>>() {
            override fun onSuccess(response: ApiResponse<Any>?, code: Int) {
                callback.onSuccess(response?.message)
            }

            override fun onError(message: String?) {
                callback.onFailed(message)
            }
        })
        return call
    }
}