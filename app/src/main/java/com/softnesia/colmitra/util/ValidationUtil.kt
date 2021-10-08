package com.softnesia.colmitra.util

import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout

/**
 * Created by Dark on 21/07/2017.
 */
object ValidationUtil {
    fun isTextEmpty(textInputLayout: TextInputLayout, errorMessage: String? = null): Boolean {
        val isEmpty = isEmpty(textInputLayout.editText!!.text?.trim())
        if (isEmpty) textInputLayout.error = errorMessage

        return isEmpty
    }

    fun isTextEmpty(editText: EditText, errorMessage: String? = null): Boolean {
        val isEmpty = isEmpty(editText.text?.trim())
        if (isEmpty) editText.error = errorMessage

        return isEmpty
    }

    fun isEmpty(charSequence: CharSequence?): Boolean {
        return charSequence == null || charSequence.isEmpty()
    }

    fun isEmailValid(editText: EditText, errorMessage: String?): Boolean {
        val isValid = isEmailValid(editText.text?.toString()?.trim())
        if (!isValid) editText.error = errorMessage
        return isValid
    }

    fun isEmailValid(textInputLayout: TextInputLayout, errorMessage: String?): Boolean {
        val isValid = isEmailValid(textInputLayout.editText!!.text?.toString()?.trim())
        if (!isValid) textInputLayout.error = errorMessage
        return isValid
    }

    fun isEmailValid(email: String?): Boolean {
        if (email == null || email.isEmpty()) return false

        var pos = email.indexOf("@")
        if (pos == -1 || pos == 0) return false // inserted email doesn't contain "@" or positioned at the first character
        pos = email.indexOf(".", pos) // Search if there is any character "." after "@"

        // inserted email doesn't contain character "." after "@" or it's at the end of the word
        return !(pos == -1 || pos == email.length - 1)
    }
}