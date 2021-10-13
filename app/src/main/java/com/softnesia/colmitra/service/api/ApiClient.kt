package com.softnesia.colmitra.service.api

import com.softnesia.colmitra.model.Collector
import com.softnesia.colmitra.model.customer.Customer
import com.softnesia.colmitra.model.account.Account
import com.softnesia.colmitra.model.account.AuthResponse
import com.softnesia.colmitra.model.customer.CustomerRepository
import com.softnesia.colmitra.model.customer.CustomerResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by Dark on 21/12/2017.
 */
interface ApiClient {
    @GET
    fun downloadFileWithDynamicUrlSync(@Url fileUrl: String?): Call<ResponseBody?>?

    //------------------ Account ----------------------
    @FormUrlEncoded
    @Headers("Accept: application/json")
    @PUT("update-surveyor.php")
    fun updateProfile(
        @Field("name") name: String,
        @Field("branchId") branchId: Int,
        @Field("position") position: String
    ): Call<ApiResponse<Account>>

    //------------------ Auth -------------------------
    @FormUrlEncoded
    @POST("login")
    @Headers("@: NoAuth")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<AuthResponse>

    @FormUrlEncoded
    @POST("register.php")
    @Headers("@: NoAuth", "Accept: application/json")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("branchId") branchId: Int,
        @Field("position") position: String
    ): Call<ApiResponse<Account>>

    @FormUrlEncoded
    @POST("reset-password.php")
    fun forgotPassword(
        @Field("email") email: String
    ): @JvmSuppressWildcards Call<ApiResponse<Any>>

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @PUT("change-password.php")
    fun updatePassword(
        @Field("oldPassword") oldPassword: String,
        @Field("newPassword") newPassword: String
    ): Call<ApiResponse<Account>>

    //------------------ Collector -------------------------

    @Headers("Accept: application/json")
    @GET("DataCollector")
    fun getCollector(): Call<Collector>

    @Headers("Accept: application/json")
    @GET("DataCollector")
    fun getCollectorSearch(
        @Query("search") search: String
    ): Call<Collector>

    @Headers("Accept: application/json")
    @GET("DataNasabah")
    fun customerDetail(
        @Query("id_nasabah") id: Long
    ): Call<CustomerResponse>

    @Headers("Accept: application/json")
    @POST("UpdateStatus")
    fun updateCustomerStatus(
        @Body body: HashMap<String, Any>
    ): Call<ApiResponse<Any>>
}