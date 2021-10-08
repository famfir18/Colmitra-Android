package com.softnesia.colmitra.ui.widget.list

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.layout_empty.view.*

/**
 * Created by Dark on 25/11/2016.
 */
class EmptyViewHolder(
    val rootView: View,
    val tvTitle: TextView? = rootView.tvTitle,
    val tvMessage: TextView? = rootView.tvMessage,
    val ivImage: ImageView? = rootView.ivImage,
    val btnAction: Button? = rootView.btnAction
) {
    var clickListener: View.OnClickListener? = null

    fun setTitleTextColor(color: Int): EmptyViewHolder {
        tvTitle!!.setTextColor(color)
        return this
    }

    fun setTitle(text: String): EmptyViewHolder {
        tvTitle!!.text = text
        return this
    }

    fun showTitle(show: Boolean): EmptyViewHolder {
        tvTitle!!.visibility = if (show) View.VISIBLE else View.GONE
        return this
    }

    fun setMessageTextColor(color: Int): EmptyViewHolder {
        tvMessage!!.setTextColor(color)
        return this
    }

    fun setMessage(text: String): EmptyViewHolder {
        tvMessage!!.text = text
        return this
    }

    fun showMessage(show: Boolean): EmptyViewHolder {
        tvMessage!!.visibility = if (show) View.VISIBLE else View.GONE
        return this
    }

    fun setIcon(iconResId: Int, show: Boolean = true): EmptyViewHolder {
        ivImage!!.setImageResource(iconResId)

        showIcon(show)
        return this
    }

    fun showIcon(show: Boolean): EmptyViewHolder {
        ivImage!!.visibility = if (show) View.VISIBLE else View.GONE
        return this
    }

    fun setupButton(
        text: String,
        listener: View.OnClickListener,
        show: Boolean = true
    ): EmptyViewHolder {
        btnAction!!.text = text
        btnAction.setOnClickListener(listener)
        clickListener = listener

        showButton(show)
        return this
    }

    fun buttonText(text: String): EmptyViewHolder {
        btnAction!!.text = text
        return this
    }

    fun buttonClickListener(listener: View.OnClickListener): EmptyViewHolder {
        btnAction!!.setOnClickListener(listener)
        clickListener = listener
        return this
    }

    fun showButton(show: Boolean): EmptyViewHolder {
        btnAction!!.visibility = if (show) View.VISIBLE else View.GONE
        return this
    }

    fun hide() {
        rootView.visibility = View.GONE
    }

    fun show() {
        rootView.visibility = View.VISIBLE
    }
}