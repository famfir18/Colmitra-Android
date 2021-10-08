package com.softnesia.colmitra.model.customer

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Customer(
    @SerializedName("ID")
    val id: Long,
    @SerializedName("USER_ID")
    val userId: String? = null,
    @SerializedName("NAMA")
    val name: String,
    @SerializedName("TELEPHONE")
    val phone: String? = null,
    @SerializedName("TELEPHONE_RUMAH")
    val cpPhone: String? = null,
    @SerializedName("TELEPHONE_KANTOR")
    val officePhone: String? = null,
    @SerializedName("ALAMAT")
    val address: String? = null,
    @SerializedName("ALAMAT_KANTOR")
    val officeAddress: String? = null,
    @SerializedName("TOTAL_TAGIHAN")
    val billNominal: Double,
    @SerializedName("CATATAN")
    val note: String?,
    @SerializedName("FOTO")
    val photoUrl: String?,
    @SerializedName("AMCOLL")
    val amcoll: Double,
    @SerializedName("STATUS_KUNJUNGAN")
    val visitStatus: Long? = null,
    @SerializedName("STATUS_PEMBAYARAN")
    val paymentStatus: Long? = null
) : Parcelable