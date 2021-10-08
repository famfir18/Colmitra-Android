package com.softnesia.colmitra.model.account

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    val success: Boolean = false,
    val message: String? = null,
    @SerializedName("api_token")
    val token: String? = null,
    val data: Account? = null
)