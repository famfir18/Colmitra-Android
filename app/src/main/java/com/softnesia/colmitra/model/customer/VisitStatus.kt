package com.softnesia.colmitra.model.customer

import com.google.gson.annotations.SerializedName

const val STATUS_VISIT_ID = 1L

data class VisitStatus(
    @SerializedName("ID")
    val id: Long,
    @SerializedName("NAMA")
    val name: String
) {
    override fun toString(): String {
        return name
    }
}