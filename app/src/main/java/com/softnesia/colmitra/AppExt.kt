package com.softnesia.colmitra

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater

//-----------------------------CONTEXT------------------------------
val Context.radius: Int
    get() = resources.getDimension(R.dimen.corner_radius).toInt()

val Context.layoutInflater: LayoutInflater
    get() = LayoutInflater.from(this)

fun Context.dimen(id: Int): Float {
    return resources.getDimension(id)
}

//-----------------------------NUMBER--------------------------------
val Number.d: Double
    get() = this.toDouble()

val Number.f: Float
    get() = this.toFloat()

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()