package com.softnesia.colmitra.ui

import android.view.View

interface ItemClickListener {
    fun onItemClicked(v: View, data: Any?, position: Int)
}