package com.softnesia.colmitra.ui.customer

import android.os.Handler
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.softnesia.colmitra.model.customer.Customer
import com.softnesia.colmitra.ui.ItemClickListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_customer.view.*

class CustomerViewHolder(val view: View, private val mListener: ItemClickListener?) :
    RecyclerView.ViewHolder(view) {

    var imgMitra = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/70/Solid_white.svg/2048px-Solid_white.svg.png"

    fun bindData(data: Customer) {
        view.tvCustomerUserId.text = data.userId
        view.tvCustomerName.text = data.name.toUpperCase()
        view.tvCustomerPhone.text = data.phone
        if (data.company == null || data.company == "Pilih Company") {
            view.tvCompany.text = "-"
        } else {
            view.tvCompany.text = data.company
        }
        view.tvMitra.text = data.mitra


//        if (data.imgMitra == null) {
//            view.iv_logo_mitra.visibility = View.GONE
//        } else {
//            imgMitra = data.imgMitra
//        }

        if (data.mitra.equals("Kredivo")) {
            imgMitra = "https://iili.io/XtQDHx.png"
        } else if (data.mitra.equals("JULO")) {
            imgMitra = "https://iili.io/Xtbpdg.png"
        } else if (data.mitra.equals("AkuLaku")) {
            imgMitra = "https://play-lh.googleusercontent.com/Y_Lqnss2E3R863fgAlc-L_TV9L_MXIkb2g0lzW0L7N2veqfUqt5mp86AtzN3oOw3EQ"
        } else if (data.mitra.equals("CashWagon")) {
            imgMitra = "https://play-lh.googleusercontent.com/GzCL8rk7ZrMHv2BTLO4Z_m0ksflo_C9XvWoc0bMBMZMAIKJvMhS9zlulkKOMAF7qSg=w220-rw"
        } else if (data.mitra.equals("INDODANA")) {
            imgMitra = "https://s.kaskus.id/c480x480/images/fjb/2020/09/15/cairkan_limit_kredivo_bri_ceria__indodana_paylater_8224754_1600165545.jpg"
        } else if (data.mitra.equals("HomeCredit Indonesia")) {
            imgMitra = "https://play-lh.googleusercontent.com/V9gHNwbAPCUF0xryuxi-we9sJ6kUOUEP06o0Ncphke7viZHW1qy4Y1CR1lVKO3auVA"
        } else if (data.mitra.equals("Dana Bagus")) {
            imgMitra = "https://iili.io/XtbFUu.png"
        } else if (data.mitra.equals("DO-IT")) {
            imgMitra = "https://iili.io/XtbtqP.png"
        } else {
            view.iv_logo_mitra.visibility = View.GONE
        }

       /* when (data.mitra) {
            "Kredivo" -> imgMitra = "https://iili.io/XtQDHx.png"
            "Julo" -> imgMitra = "https://iili.io/Xtbpdg.png"
            "akulaku" -> imgMitra = "https://play-lh.googleusercontent.com/Y_Lqnss2E3R863fgAlc-L_TV9L_MXIkb2g0lzW0L7N2veqfUqt5mp86AtzN3oOw3EQ"
            "cashwagon" -> imgMitra = "https://play-lh.googleusercontent.com/GzCL8rk7ZrMHv2BTLO4Z_m0ksflo_C9XvWoc0bMBMZMAIKJvMhS9zlulkKOMAF7qSg=w220-rw"
            "indodana" -> imgMitra = "https://s.kaskus.id/c480x480/images/fjb/2020/09/15/cairkan_limit_kredivo_bri_ceria__indodana_paylater_8224754_1600165545.jpg"
            "HomeCredit Indonesia" -> imgMitra = "https://play-lh.googleusercontent.com/V9gHNwbAPCUF0xryuxi-we9sJ6kUOUEP06o0Ncphke7viZHW1qy4Y1CR1lVKO3auVA"
            "Dana Bagus" -> imgMitra = "https://iili.io/XtbFUu.png"
            "DO-IT" -> imgMitra = "https://iili.io/XtbtqP.png"
            else -> {
                view.iv_logo_mitra.visibility = View.GONE
            }
        }*/

        Picasso.get()
            .load(imgMitra)
            .into(view.iv_logo_mitra)

        itemView.setOnClickListener { view: View? ->
            mListener?.onItemClicked(view!!, data, adapterPosition)
        }
    }
}