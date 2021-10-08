package com.softnesia.colmitra.service.api

/**
 * https://futurestud.io/tutorials/retrofit-2-simple-error-handling
 */
class ApiResponse<T : Any?>(
    val success: Boolean = false,
    val message: String? = null,
    val data: T? = null
)