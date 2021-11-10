package com.softnesia.colmitra.ui.customer

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.softnesia.colmitra.model.customer.Customer
import com.softnesia.colmitra.ui.ItemClickListener
import kotlinx.android.synthetic.main.item_customer.view.*

class CustomerViewHolder(val view: View, private val mListener: ItemClickListener?) :
    RecyclerView.ViewHolder(view) {

    fun bindData(data: Customer) {
        view.tvNumberHeader.text = (adapterPosition + 1).toString()
        view.tvCustomerUserId.text = data.userId
        view.tvCustomerName.text = data.name
        view.tvCustomerPhone.text = data.phone
        if (data.company == null || data.company == "Pilih Company") {
            view.tvCompany.text = "-"
        } else {
            view.tvCompany.text = data.company
        }
        view.tvMitra.text = data.mitra

        itemView.setOnClickListener { view: View? ->
            mListener?.onItemClicked(view!!, data, adapterPosition)
        }
    }
}