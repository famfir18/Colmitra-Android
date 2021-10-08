package com.softnesia.colmitra.model.account

import com.google.gson.annotations.SerializedName

/**
 * Created by Dark on 21/12/2017.
 */
class Auth(
//    @SerializedName("refresh_token")
//    var refreshToken: String? = null
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_type")
    val tokenType: String,
    val user: Account
)