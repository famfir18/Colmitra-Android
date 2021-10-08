package com.softnesia.colmitra.ui.widget.message

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.os.Build
import android.os.Handler
import android.text.TextUtils
import android.view.View
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.softnesia.colmitra.R
import com.softnesia.colmitra.util.LDate.changeFormat
import com.softnesia.colmitra.util.LDate.defaultDateFormat
import java.util.*

object DialogComposer {
    fun showErrorDialog(context: Context, message: String): MaterialDialog {
        return MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            message(text = message)
        }
    }

    fun messageDialog(
        context: Context,
        title: String? = null,
        message: String,
        buttonText: String? = null,
        buttonListener: DialogCallback? = null
    ): MaterialDialog {
        return MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            if (title != null) title(text = title)
            message(text = message)
            buttonText?.also { positiveButton(text = it) }
            buttonListener?.also {
                positiveButton {
                    // Delay dismiss to make the transition smooth
                    Handler().postDelayed(
                        { dismiss() }, 1000
                    )

                    buttonListener.invoke(it)
                }
            }
        }
    }

    fun logOutDialog(context: Context, logOutListener: View.OnClickListener): MaterialDialog {
        return MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            message(R.string.all_log_out_confirmation)
            positiveButton(R.string.all_log_out) {
                logOutListener.onClick(it.view)
            }
            negativeButton(R.string.all_cancel)
        }
    }

    fun displayBirthdateDialog(
        context: Context,
        date_: String,
        listener: OnDateSetListener
    ): DatePickerDialog {
        val date =
            if (!TextUtils.isEmpty(date_)) date_
            else {
                // Set default year in 2000
                val calendar = Calendar.getInstance()
                calendar[Calendar.YEAR] = 2000
                changeFormat(calendar.time, defaultDateFormat())
            }

        val dates = date.split("-").toTypedArray()
        val year = dates[0].toInt()
        val month = dates[1].toInt() - 1
        val day = dates[2].toInt()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /*
             * Force date picker to use spinner style in OS Lollipop above
             * Material Date Picker for birthdate is a bad UX
             */
            DatePickerDialog(
                context,
                R.style.DatePickerDialogTheme,
                listener,
                year,
                month,
                day
            )
        } else {
            DatePickerDialog(context, listener, year, month, day)
        }
    }

    fun displayDateDialog(
        context: Context,
        date: String,
        listener: OnDateSetListener
    ): DatePickerDialog {
        var date = date
        if (TextUtils.isEmpty(date)) {
            // Set default to today if empty
            val calendar = Calendar.getInstance()
            date = changeFormat(calendar.time, defaultDateFormat())
        }
        val dates = date.split("-").toTypedArray()
        val year = dates[0].toInt()
        val month = dates[1].toInt() - 1
        val day = dates[2].toInt()

        return DatePickerDialog(context, listener, year, month, day)
    }
}