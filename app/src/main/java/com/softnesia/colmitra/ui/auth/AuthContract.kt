package com.softnesia.colmitra.ui.auth

import com.softnesia.colmitra.ui.BaseContract

/**
 * Created by Dark on 21/01/2018.
 */
class AuthContract {
    interface LoginView : BaseContract.RemoteListView {
        fun onLoginSucceed()
    }

    interface LoginAction {
        fun login(username: String, password: String)
    }
}