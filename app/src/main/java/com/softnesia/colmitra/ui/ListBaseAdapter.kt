package com.softnesia.colmitra.ui

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Dark on 06/01/2017.
 */

abstract class ListBaseAdapter<T>(
    protected var mItemList: MutableList<T>,
    protected var mListener: ItemClickListener?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected fun updateList(itemList: MutableList<T>) {
        mItemList = itemList
        notifyDataSetChanged()
    }

    fun addItems(itemList: List<T>) {
        mItemList.addAll(itemList)
        notifyDataSetChanged()
    }

    fun updateItem(index: Int, data: T) {
        mItemList[index] = data
        notifyItemChanged(index)
    }

    fun addItem(index: Int, data: T) {
        mItemList.add(index, data)
        notifyItemInserted(index)
    }

    fun getItem(position: Int): T {
        return mItemList[position]
    }

    fun getItems(): MutableList<T> {
        return mItemList
    }

    override fun getItemCount(): Int {
        return mItemList.size
    }

    fun clear() {
        mItemList.clear()
        notifyDataSetChanged()
    }
}
