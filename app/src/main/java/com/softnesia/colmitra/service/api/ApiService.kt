package com.softnesia.colmitra.service.api

import com.google.gson.GsonBuilder
import com.softnesia.colmitra.BuildConfig
import com.softnesia.colmitra.service.api.UnsafeOkHttpClient.unsafeOkHttpClientBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Dark on 21/12/2017.
 */
object ApiService {
    @get:Synchronized
    val client: ApiClient = initiate()

    fun generateRetrofit(baseUrl: String): Retrofit {
        val gson = GsonBuilder()
            .setLenient()
            .serializeNulls()
            .create()

        val httpClient: OkHttpClient.Builder = if (BuildConfig.DEBUG) {
            unsafeOkHttpClientBuilder
        } else OkHttpClient.Builder()

        httpClient.authenticator(TokenAuthenticator())
            .addNetworkInterceptor(TokenInterceptor())
            .connectTimeout(1, TimeUnit.MINUTES)
            .callTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)

        val logging = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

        // add logging as interceptor
        if (!httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging)
        }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient.build())
            .build()
    }

    private fun initiate(): ApiClient {
        val retrofit = generateRetrofit(BuildConfig.BASE_URL)
        return retrofit.create(ApiClient::class.java)
    }
}