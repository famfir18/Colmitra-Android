package com.softnesia.colmitra.ui.auth.login

import com.softnesia.colmitra.config.Session
import com.softnesia.colmitra.model.account.Account
import com.softnesia.colmitra.model.account.AccountRepository
import com.softnesia.colmitra.model.account.AuthResponse
import com.softnesia.colmitra.ui.auth.AuthContract
import com.softnesia.colmitra.util.network.ResponseCallback

/**
 * Created by Dark on 21/01/2018.
 */
class LoginPresenter internal constructor(private val view: AuthContract.LoginView) :
    AuthContract.LoginAction {

    override fun login(username: String, password: String) {
        view.setProgressIndicator(true)
        AccountRepository.login(username, password, object : ResponseCallback<AuthResponse> {
            override fun onSuccess(data: AuthResponse) {
                Session.saveAuth(data.token!!)
                Account.getInstance().save(data.data)
                view.onLoginSucceed()
            }

            override fun onFailed(message: String?) {
                view.setProgressIndicator(false)
                view.showError(message)
            }

            override fun onEmpty(message: String?) {}
        })
    }
}