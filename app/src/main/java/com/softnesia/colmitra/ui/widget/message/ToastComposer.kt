package com.softnesia.colmitra.ui.widget.message

import android.content.Context
import android.widget.Toast
import com.softnesia.colmitra.R

/**
 * Created by Dark on 18/07/2017.
 */
object ToastComposer {
    fun dataEmpty(context: Context, _message: String?) {
        val message = _message ?: context.getString(R.string.error_data_empty)
        show(context, message)
    }

    fun loadError(context: Context, _message: String?) {
        val message = _message ?: context.getString(R.string.error_loading_failed)
        showErrorType(context, message)
    }

    fun sendFailed(context: Context, _message: String?) {
        val message = _message ?: context.getString(R.string.error_send_data_failed)
        showErrorType(context, message)
    }

    fun networkUnavailable(context: Context) {
        showErrorType(context, R.string.error_network_unavailable)
    }

    fun showErrorType(context: Context, textResId: Int) {
        showErrorType(context, context.getString(textResId))
    }

    fun showErrorType(context: Context, _message: String?) {
        val message = _message ?: context.getString(R.string.error_occurred)
        show(context, message)
    }

    fun show(context: Context, textResId: Int, colorResId: Int = 0) {
        show(context, context.getString(textResId), colorResId)
    }

    @JvmOverloads
    fun show(context: Context, text: CharSequence?, colorResId: Int = 0) {
        val toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
        toast.show()
    }
}