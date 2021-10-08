package com.softnesia.colmitra.model.customer

import com.google.gson.annotations.SerializedName

/**
 * https://futurestud.io/tutorials/retrofit-2-simple-error-handling
 */
class CustomerResponse(
    val message: String? = null,
    @SerializedName("data")
    val customer: Customer,
    @SerializedName("bayar")
    val paymentStatuses: List<PaymentStatus>,
    @SerializedName("status")
    val visitStatuses: List<VisitStatus>
)