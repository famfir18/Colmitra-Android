package com.softnesia.colmitra.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.softnesia.colmitra.model.customer.Customer
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Collector(
    val success: Boolean = false,
    val message: String,
    @SerializedName("nama")
    val name: String,
    @SerializedName("foto")
    val avatar: String? = null,
    @SerializedName("data")
    val customers: List<Customer> = listOf()
) : Parcelable