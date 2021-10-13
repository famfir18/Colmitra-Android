package com.softnesia.colmitra.model.account

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.softnesia.colmitra.config.MyApplication
import com.softnesia.colmitra.util.security.ObscuredSharedPreferences

class Account private constructor() {
    @Transient
    private var editor: ObscuredSharedPreferences.Editor? = null

    @Transient
    private var bulkUpdating = false

    val id: Long = 0
    val username: String? = null

    @SerializedName("nama")
    var name: String? = null

    @SerializedName("foto")
    var avatar: String? = null

    @SerializedName("id_login_collector")
    val collectionId: Long = 0

    @SerializedName("api_token")
    var token: String? = null

    fun save(): Account? {
        editor = sp.edit()
        val dataString = Gson().toJson(instance)
        editor!!.putString(Key.ACCOUNT, dataString)
        editor!!.apply()
        return instance
    }

    fun save(account: Account?): Account? {
        instance = account
        save()
        return instance
    }

    fun deleteCurrentUser() {
        doEdit()
        for (key in Key.KEYS) {
            editor!!.remove(key)
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
        const val ACCOUNT = "account_key"
        val KEYS = arrayOf(
            ACCOUNT
        )
    }

    companion object {
        @Transient
        @Volatile
        private var instance: Account? = null

        @Transient
        private var sp: ObscuredSharedPreferences = MyApplication.instance!!.sp

        fun getInstance(): Account {
            if (instance == null) {
                val accountString: String? = sp.getString(Key.ACCOUNT, "")
                instance = Gson().fromJson(accountString, Account::class.java)
            }

            return instance ?: Account()
        }
    }
}