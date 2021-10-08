package com.softnesia.colmitra.model.account

import com.softnesia.colmitra.service.api.ApiResponse
import com.softnesia.colmitra.service.api.ApiService.client
import com.softnesia.colmitra.service.api.DefaultCallback
import com.softnesia.colmitra.util.network.ResponseCallback
import retrofit2.Call

/**
 * Created by Dark on 21/01/2018.
 */
object AccountRepository {
    fun login(username: String, password: String, callback: ResponseCallback<AuthResponse>): Call<*> {
        val call = client.login(username, password)

        call.enqueue(object : DefaultCallback<AuthResponse>() {
            override fun onSuccess(response: AuthResponse?, code: Int) {
                callback.onSuccess(response!!)
            }

            override fun onError(message: String?) {
                callback.onFailed(message)
            }
        })
        return call
    }

//    fun logout(callback: Save<Boolean>): Call<*> {
//        val call = client.logout()
//
//        call.enqueue(object : Callback<Void> {
//            override fun onResponse(call: Call<Void>, response: Response<Void>) {
//                callback.onDataSaved(true)
//            }
//
//            override fun onFailure(call: Call<Void>, t: Throwable) {
//                callback.onSaveFailed(t.message)
//            }
//        })
//        return call
//    }

//    fun profile(callback: Load<Account?>): Call<*> {
//        val call = client.profile()
//
//        call.enqueue(object : DefaultCallback<ApiResponse<Account>>() {
//            override fun onSuccess(response: ApiResponse<Account>?, code: Int) {
//                callback.onDataLoaded(response?.data)
//            }
//
//            override fun onError(message: String?) {
//                callback.onFailed(message)
//            }
//        })
//        return call
//    }
}