package com.softnesia.colmitra.config

import com.softnesia.colmitra.util.security.ObscuredSharedPreferences

/**
 * Created by Dark on 15/12/2015.
 */
object Settings {
    private var sp: ObscuredSharedPreferences = MyApplication.instance!!.sp
    private var editor: ObscuredSharedPreferences.Editor? = null
    private var bulkUpdating = false

    fun put(key: String, `val`: String) {
        doEdit()
        editor!!.putString(key, `val`)
        doApply()
    }

    fun put(key: String, `val`: Int) {
        doEdit()
        editor!!.putInt(key, `val`)
        doApply()
    }

    fun put(key: String, `val`: Boolean) {
        doEdit()
        editor!!.putBoolean(key, `val`)
        doApply()
    }

    fun put(key: String, `val`: Float) {
        doEdit()
        editor!!.putFloat(key, `val`)
        doApply()
    }

    /**
     * Convenience method for storing doubles.
     *
     *
     * There may be instances where the accuracy of a double is desired.
     * SharedPreferences does not handle doubles so they have to
     * cast to and from String.
     *
     * @param key The enum of the preference to store.
     * @param val The new value for the preference.
     */
    fun put(key: String, `val`: Double) {
        doEdit()
        editor!!.putString(key, `val`.toString())
        doApply()
    }

    fun put(key: String, `val`: Long) {
        doEdit()
        editor!!.putLong(key, `val`)
        doApply()
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        return sp.getString(key, defaultValue)
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sp.getInt(key, defaultValue)
    }

    fun getLong(key: String, defaultValue: Long = 0): Long {
        return sp.getLong(key, defaultValue)
    }

    fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return sp.getFloat(key, defaultValue)
    }

    /**
     * Convenience method for retrieving doubles.
     *
     *
     * There may be instances where the accuracy of a double is desired.
     * SharedPreferences does not handle doubles so they have to
     * cast to and from String.
     *
     * @param key The enum of the preference to fetch.
     */
    fun getDouble(key: String, defaultValue: Double = 0.0): Double {
        return try {
            java.lang.Double.valueOf(sp.getString(key, defaultValue.toString())!!)
        } catch (nfe: NumberFormatException) {
            defaultValue
        }
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sp.getBoolean(key, defaultValue)
    }

    /**
     * Remove keys from SharedPreferences.
     *
     * @param keys The enum of the key(s) to be removed.
     */
    fun remove(keys: Array<String>) {
        doEdit()
        keys.forEach {
            editor!!.remove(it)
        }
        doApply()
    }

    fun edit() {
        editor = sp.edit()
        bulkUpdating = true
    }

    fun apply() {
        editor!!.apply()
        editor = null
        bulkUpdating = false
    }

    private fun doEdit() {
        if (!bulkUpdating && editor == null) {
            editor = sp.edit()
        }
    }

    private fun doApply() {
        if (!bulkUpdating && editor != null) {
            editor!!.apply()
            editor = null
        }
    }

    object Key {
        const val ACCESS_TOKEN = "access_token_key"
        const val REFRESH_TOKEN = "refresh_token_key"
        const val TOKEN_TYPE = "token_type_key"
        const val HAS_LOGGED_IN = "logged_in_key"

        @JvmField
        val KEYS = arrayOf(
            ACCESS_TOKEN,
            REFRESH_TOKEN,
            TOKEN_TYPE,
            HAS_LOGGED_IN
        )
    }
}