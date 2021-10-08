package com.softnesia.colmitra.ui.customer

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softnesia.colmitra.R
import com.softnesia.colmitra.layoutInflater
import com.softnesia.colmitra.model.customer.Customer
import com.softnesia.colmitra.ui.ItemClickListener
import com.softnesia.colmitra.ui.ListBaseAdapter

/**
 * Created by Dark on 14/12/2017.
 */
class CustomerAdapter(
    itemList: MutableList<Customer>, listener: ItemClickListener?
) : ListBaseAdapter<Customer>(itemList, listener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = parent.context.layoutInflater.inflate(R.layout.item_customer, parent, false)
        return CustomerViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val vh = holder as CustomerViewHolder
        vh.bindData(getItem(position))
    }

    override fun getItemId(position: Int): Long {
        return if (hasStableIds()) getItem(position).id else super.getItemId(position)
    }
}