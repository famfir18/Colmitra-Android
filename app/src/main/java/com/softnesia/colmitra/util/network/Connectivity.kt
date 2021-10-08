/*
 * Copyright (c) 2017 Emil Davtyan
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package com.softnesia.colmitra.util.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.telephony.TelephonyManager

/**
 * Check device's network connectivity and speed
 *
 * @author emil http://stackoverflow.com/users/220710/emil
 */
object Connectivity {
    /**
     * Get the network info
     *
     * @param context
     * @return
     */
    fun getNetworkInfo(context: Context?): NetworkInfo? {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return cm?.activeNetworkInfo
    }

    /**
     * Check if there is any connectivity
     *
     * @param context
     * @return
     */
    fun isConnected(context: Context?): Boolean {
        return getNetworkInfo(context)?.isConnected ?: false
    }

    /**
     * Check if there is any connectivity to a Wifi network
     *
     * @param context
     * @return
     */
    fun isConnectedWifi(context: Context): Boolean {
        return getConnectionType(context) == WIFI
    }

    /**
     * Check if there is any connectivity to a mobile network
     *
     * @param context
     * @return
     */
    fun isConnectedMobile(context: Context): Boolean {
        return getConnectionType(context) == MOBILE
    }

    /**
     * Check if there is fast connectivity
     *
     * @param context
     * @return
     */
    fun isConnectedFast(context: Context): Boolean {
        val info = getNetworkInfo(context) ?: return false

        val type = getConnectionType(context)
        return info.isConnected && isConnectionFast(
            type,
            info.subtype
        )
    }

    /**
     * Check if the connection is fast
     *
     * @param type
     * @param subType
     * @return
     */
    private fun isConnectionFast(type: Int, subType: Int): Boolean {
        return when (type) {
            WIFI -> {
                true
            }
            MOBILE -> {
                when (subType) {
                    TelephonyManager.NETWORK_TYPE_1xRTT, // ~ 50-100 kbps
                    TelephonyManager.NETWORK_TYPE_CDMA, // ~ 14-64 kbps
                    TelephonyManager.NETWORK_TYPE_EDGE, // ~ 50-100 kbps
                    TelephonyManager.NETWORK_TYPE_GPRS -> false // ~ 100 kbps

                    TelephonyManager.NETWORK_TYPE_EVDO_0, // ~ 400-1000 kbps
                    TelephonyManager.NETWORK_TYPE_EVDO_A, // ~ 600-1400 kbps
                    TelephonyManager.NETWORK_TYPE_HSDPA, // ~ 2-14 Mbps
                    TelephonyManager.NETWORK_TYPE_HSPA, // ~ 700-1700 kbps
                    TelephonyManager.NETWORK_TYPE_HSUPA, // ~ 1-23 Mbps
                    TelephonyManager.NETWORK_TYPE_UMTS -> true // ~ 400-7000 kbps
                    /*
                     * Above API level 7, make sure to set android:targetSdkVersion
                     * to appropriate level to use these
                     */
                    TelephonyManager.NETWORK_TYPE_EHRPD, // ~ 1-2 Mbps
                    TelephonyManager.NETWORK_TYPE_EVDO_B, // ~ 5 Mbps
                    TelephonyManager.NETWORK_TYPE_HSPAP, // ~ 10-20 Mbps
                    TelephonyManager.NETWORK_TYPE_LTE -> true // ~ 10+ Mbps

                    TelephonyManager.NETWORK_TYPE_IDEN, // ~ 25 kbps
                    TelephonyManager.NETWORK_TYPE_UNKNOWN -> false

                    else -> false
                }
            }
            else -> {
                false
            }
        }
    }

    fun getConnectionType(context: Context): Int {
        var result = NO_CONNECTION // Returns connection type. 0: none; 1: mobile data; 2: wifi
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    if (hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        result = WIFI
                    } else if (hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        result = MOBILE
                    }
                }
            }
        } else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        result = WIFI
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
                        result = MOBILE
                    }
                }
            }
        }
        return result
    }

    private const val NO_CONNECTION = 1
    private const val WIFI = 2
    private const val MOBILE = 3
}