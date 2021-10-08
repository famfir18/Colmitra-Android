package com.softnesia.colmitra.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.softnesia.colmitra.R
import com.softnesia.colmitra.ui.BaseActivity
import com.softnesia.colmitra.ui.auth.AuthContract
import com.softnesia.colmitra.ui.main.MainActivity
import com.softnesia.colmitra.ui.widget.message.ToastComposer
import com.softnesia.colmitra.util.ValidationUtil
import com.softnesia.colmitra.util.ViewUtil
import com.softnesia.colmitra.util.network.Connectivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.toolbar.*

class LoginActivity : BaseActivity(), AuthContract.LoginView {
    private lateinit var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        rootLayout.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        setToolbarNoTitle(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        presenter = LoginPresenter(this)

        btnSubmit.setOnClickListener { doLogin() }
    }

    private fun doLogin() {
        if (!Connectivity.isConnected(this)) {
            ToastComposer.networkUnavailable(this)
            return
        }

        if (!isFormValid) {
            ToastComposer.showErrorType(this, R.string.error_form_not_complete)
            return
        }

        ViewUtil.hideKeyboard(this)

        val email = etUsername.text.toString()
        val password = etPassword.text.toString()
        presenter.login(email, password)
    }

    private val isFormValid: Boolean
        get() {
            var isValid = true

            if (ValidationUtil.isTextEmpty(tilUsername, getString(R.string.error_must_not_empty))) {
                isValid = false
            }

            if (ValidationUtil.isTextEmpty(tilPassword, getString(R.string.error_must_not_empty))) {
                isValid = false
            }

            return isValid
        }

    override fun setProgressIndicator(active: Boolean) {
        btnSubmit.visibility = if (active) View.GONE else View.VISIBLE
        progressBar.visibility = if (active) View.VISIBLE else View.GONE
    }

    override fun showEmpty(message: String?) {
        if (isFinishing) return
        ToastComposer.dataEmpty(this, message)
    }

    override fun showError(message: String?) {
        if (isFinishing) return
        ToastComposer.loadError(this, message)
    }

    override fun onLoginSucceed() {
        if (isFinishing) return

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}