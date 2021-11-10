package com.softnesia.colmitra.ui

import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import com.softnesia.colmitra.R
import com.softnesia.colmitra.config.Session
import com.softnesia.colmitra.model.UnauthorizedEvent
import com.softnesia.colmitra.model.account.Account
import com.softnesia.colmitra.ui.auth.login.LoginActivity
import com.softnesia.colmitra.ui.widget.message.ToastComposer
import com.yayandroid.locationmanager.base.LocationBaseActivity
import com.yayandroid.locationmanager.configuration.Configurations
import com.yayandroid.locationmanager.configuration.LocationConfiguration
import kotlinx.android.synthetic.main.toolbar.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Dark on 29/05/2016.
 */
open class BaseActivity : LocationBaseActivity() {

    // A base presenter that functions to cancel all Retrofit calls on onDestroy
    protected var basePresenter: BasePresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Disable screen shot
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    fun setToolbar(toolbar: Toolbar, titleResId: Int) {
        setToolbar(toolbar, getString(titleResId))
    }

    fun setToolbar(toolbar: Toolbar, title: CharSequence) {
        setSupportActionBar(toolbar)

        if (TextUtils.isEmpty(title)) return

        supportActionBar?.setDisplayShowTitleEnabled(false)

        toolbar.tvTitle.text = title
    }

    fun setToolbarNoTitle(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    fun setLogo(logoResId: Int) {
        supportActionBar?.setLogo(logoResId)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    /*
     * Will be called in case refreshing token has failed.
     * Do log out.
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onUnauthorizedEvent(event: UnauthorizedEvent?) {
        Account.getInstance().deleteCurrentUser()
        Session.logout()

        ToastComposer.show(applicationContext, R.string.error_unauthorized, 0)

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onLocationChanged(location: Location) {

    }

    override fun onLocationFailed(type: Int) {
        ToastComposer.showErrorType(this, "Getting user location failed: $type")
    }

    override fun getLocationConfiguration(): LocationConfiguration {
        return Configurations.defaultConfiguration(
            getString(R.string.message_location_rationale),
            getString(R.string.message_gps_rationale)
        )
    }

    override fun onDestroy() {
        basePresenter?.onDestroy()
        basePresenter = null

        super.onDestroy()
    }

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }
}
