package com.softnesia.colmitra.config

object Session {
    fun saveAuth(token: String) {
        Settings.put(Settings.Key.TOKEN_TYPE, "bearer")
        Settings.put(Settings.Key.ACCESS_TOKEN, token)

        loggedIn = true
    }

    val token: String
        get() {
            val tokenType: String? = Settings.getString(Settings.Key.TOKEN_TYPE)
            val token: String? = Settings.getString(Settings.Key.ACCESS_TOKEN)
            return "$tokenType $token"
        }

    var loggedIn: Boolean
        set(value) {
            if (value) Settings.put(Settings.Key.HAS_LOGGED_IN, value)
            else Settings.remove(Settings.Key.KEYS)
        }
        get() = Settings.getBoolean(Settings.Key.HAS_LOGGED_IN)

    fun logout() {
        loggedIn = false
    }
}