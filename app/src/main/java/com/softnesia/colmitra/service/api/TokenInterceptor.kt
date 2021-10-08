package com.softnesia.colmitra.service.api

import com.softnesia.colmitra.config.Session
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * An interceptor to add token to header for the request
 */
class TokenInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val customAnnotations = request.headers().values("@")

        // Remove all headers with key @ (custom header)
        val requestBuilder = request.newBuilder().removeHeader("@")

        // Only add Authorization header if the request doesn't contain NoAuth header
        // https://stackoverflow.com/a/37823425/2178568
        if (!customAnnotations.contains("NoAuth")) {
            val token = Session.token
            requestBuilder.header("Authorization", token)
        }
        request = requestBuilder.build()

        return chain.proceed(request)
    }
}