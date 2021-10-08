package com.softnesia.colmitra.util

import android.text.TextUtils
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.ParseException
import java.util.*
import kotlin.math.ln
import kotlin.math.pow

object LNumber {
    fun abbreviateNumber(number: Long): String {
        if (number < 1000) return "" + number
        val exp = (ln(number.toDouble()) / ln(1000.0)).toInt()
        return String.format(
            "%.1f %c",
            number / 1000.0.pow(exp.toDouble()),
            "kMGTPE"[exp - 1]
        )
    }

    fun thousandSeparator(value: Double, abbreviate: Boolean): String {
        if (abbreviate) {
            var index = 0
            var suffix = ""
            var temp = value
            while (temp > 1000) {
                temp /= 1000.0
                index++
            }
            temp = decimalLimiter(temp.toString(), 2)
            if (index == 1) suffix = "K" else if (index == 2) suffix = "M"

            // Convert temp to int if it doesn't need a decimal (in case likeComment n.0)
            return if (temp % 1 == 0.0) ("${temp.toInt()} $suffix") else ("${temp.toString()} $suffix")
        }

        val numberFormat = DecimalFormat()
        return numberFormat.format(value)
    }

    fun thousandDotSeparator(value: Double): String {
        val symbols = DecimalFormatSymbols()
        symbols.groupingSeparator = '.'
        symbols.decimalSeparator = ','
        val numberFormat = DecimalFormat()
        numberFormat.decimalFormatSymbols = symbols
        return numberFormat.format(value)
    }

    fun pad(value: Int): String {
        return if (value < 10) "0$value" else value.toString()
    }

    fun decimalLimiter(value: String, limitValue: Int): Double {
        val dValue = value.replace(",", ".").toDouble()
        return decimalLimiter(dValue, limitValue)
    }

    fun decimalLimiter(value: Double, limitValue: Int): Double {
        if (value == 0.0) return value

        /*
         * limit to limitValue decimal numbers
         * change decimal format using ".", since there are some phones that uses "," as decimal format
         */
        var limiter = "#0."
        for (i in 0 until limitValue) {
            limiter += "#"
        }
        val df = DecimalFormat(limiter)
        val dfs = DecimalFormatSymbols()
        dfs.decimalSeparator = '.'
        df.decimalFormatSymbols = dfs
        val formattedDistance = df.format(value)
        return formattedDistance.toDouble()
    }

    fun currencyFormat(value: Double, symbols: DecimalFormatSymbols?): String {
        val numberFormat = DecimalFormat.getCurrencyInstance() as DecimalFormat
        numberFormat.decimalFormatSymbols = symbols
        return numberFormat.format(value)
    }

    fun getRupiahCurrencySymbols(internationalCurrency: Boolean): DecimalFormatSymbols {
        val symbols = DecimalFormatSymbols()
        symbols.currencySymbol = if (internationalCurrency) "IDR" else "Rp "
        symbols.groupingSeparator = '.'
        symbols.monetaryDecimalSeparator = ','
        return symbols
    }

    fun formatToNumber(value: String?, symbols: DecimalFormatSymbols?): Double {
        val df = DecimalFormat()
        df.decimalFormatSymbols = symbols
        return try {
            df.parse(value).toDouble()
        } catch (e: ParseException) {
            e.printStackTrace()
            0.0
        }
    }

    fun getDecimalFormatSymbols(commaAsDecimal: Boolean): DecimalFormatSymbols {
        val symbols = DecimalFormatSymbols()
        symbols.decimalSeparator = if (commaAsDecimal) ',' else '.'
        symbols.groupingSeparator = if (commaAsDecimal) '.' else ','
        return symbols
    }

    // Change formatted number of currency to integer value
    fun currencyToNumber(value: String): Int {
        if (TextUtils.isEmpty(value)) return 0
        val cleanString = value.replace("\\D".toRegex(), "")
        try {
            return NumberFormat.getNumberInstance(Locale.getDefault()).parse(cleanString).toInt()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return cleanString.toInt()
    }
}