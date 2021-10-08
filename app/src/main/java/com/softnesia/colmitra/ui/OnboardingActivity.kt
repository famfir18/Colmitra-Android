package com.softnesia.colmitra.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.softnesia.colmitra.R
import com.softnesia.colmitra.config.Settings
import com.softnesia.colmitra.ui.auth.login.LoginActivity
import com.softnesia.colmitra.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_onboarding.*

class OnboardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val loggedIn = Settings.getBoolean(Settings.Key.HAS_LOGGED_IN)

        if (loggedIn) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            finish()
        }

        setContentView(R.layout.activity_onboarding)

        rootLayout.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        btnNext.setOnClickListener { startActivity(Intent(this, LoginActivity::class.java)) }
    }
}