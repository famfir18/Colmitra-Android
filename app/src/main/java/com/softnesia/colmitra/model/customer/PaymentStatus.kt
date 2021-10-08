package com.softnesia.colmitra.model.customer

import com.google.gson.annotations.SerializedName

const val STATUS_PTP_ID = 2L

data class PaymentStatus(
    @SerializedName("ID")
    val id: Long,
    @SerializedName("NAMA")
    val name: String
) {
    override fun toString(): String {
        return name
    }
}