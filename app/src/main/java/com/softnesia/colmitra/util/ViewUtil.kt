package com.softnesia.colmitra.util

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager

/**
 * Created by Dark on 02/08/2017.
 */
object ViewUtil {
    fun getScreenWidth(ctx: Context): Int {
        // Get screen's width size
        val wm = ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.x
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun toggleKeyboard(activity: Activity, show: Boolean) {
        activity.window.setSoftInputMode(
            if (show) WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
            else WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
        )
    }

    fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun scaleWidthByScreenRatio(view: View, ratio: Float) {
        val vto = view.viewTreeObserver
        vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                view.viewTreeObserver.removeOnPreDrawListener(this)
                val width = getScreenWidth(view.context)
                view.layoutParams.width = (width * ratio).toInt()
                return true
            }
        })
    }

    fun scaleViewByRatio(view: View, ratio: Float) {
        val vto = view.viewTreeObserver
        vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                view.viewTreeObserver.removeOnPreDrawListener(this)
                val width = view.width
                view.layoutParams.height = (width / ratio).toInt()
                return true
            }
        })
    }

    fun scaleViewAsSquare(view: View) {
        val vto = view.viewTreeObserver
        vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                view.viewTreeObserver.removeOnPreDrawListener(this)
                val width = view.width
                view.layoutParams.height = width
                return true
            }
        })
    }
}