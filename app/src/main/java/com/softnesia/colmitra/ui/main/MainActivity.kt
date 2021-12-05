package com.softnesia.colmitra.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.contains
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.javiersantos.appupdater.AppUpdater
import com.github.javiersantos.appupdater.enums.Display
import com.github.javiersantos.appupdater.enums.UpdateFrom
import com.jrtracking.surveyor.ui.ListBaseActivity
import com.mikepenz.materialdrawer.holder.StringHolder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.descriptionText
import com.mikepenz.materialdrawer.model.interfaces.iconRes
import com.mikepenz.materialdrawer.model.interfaces.iconUrl
import com.mikepenz.materialdrawer.model.interfaces.nameText
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerImageLoader
import com.mikepenz.materialdrawer.util.addItems
import com.mikepenz.materialdrawer.util.getPlaceHolder
import com.mikepenz.materialdrawer.widget.AccountHeaderView
import com.softnesia.colmitra.R
import com.softnesia.colmitra.RC_UPDATE_CUSTOMER
import com.softnesia.colmitra.config.Settings
import com.softnesia.colmitra.model.Collector
import com.softnesia.colmitra.model.customer.Customer
import com.softnesia.colmitra.model.account.Account
import com.softnesia.colmitra.ui.ItemClickListener
import com.softnesia.colmitra.ui.auth.login.LoginActivity
import com.softnesia.colmitra.ui.customer.CustomerAdapter
import com.softnesia.colmitra.ui.customer.CustomerDetailActivity
import com.softnesia.colmitra.ui.widget.message.DialogComposer
import com.softnesia.colmitra.ui.widget.message.ToastComposer
import com.softnesia.colmitra.util.ViewUtil
import com.softnesia.colmitra.util.network.Connectivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.material_drawer_header.view.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*
import java.util.Locale.filter

class MainActivity : ListBaseActivity(), MainContract.MainView, ItemClickListener {
    private lateinit var presenter: MainPresenter
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var accountHeader: AccountHeaderView

    private var itemList: MutableList<Customer> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rootLayout.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        setToolbarNoTitle(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initDrawer()

        srlRefresh = findViewById(R.id.srlRefresh)
        rvList = findViewById(R.id.rvCustomers)

        presenter = MainPresenter(this)
        basePresenter = presenter

        initEmptyView(vEmpty)?.apply {
            buttonClickListener(View.OnClickListener { loadData() })
        }

        bindUserData(Account.getInstance())
        autoUpdate()
        loadData()
    }

    override fun onResume() {
        super.onResume()
        autoUpdate()
    }

    private fun autoUpdate() {
        val appUpdater = AppUpdater(this)
            .setUpdateFrom(UpdateFrom.JSON)
            .setUpdateJSON("https://raw.githubusercontent.com/famfir18/UpdaterJson/master/update.json")
            .setButtonDoNotShowAgain("")
            .setButtonDismiss("")
            .setCancelable(true)
            .setIcon(R.drawable.logo)
            .showAppUpdated(false)
            .setCancelable(false)
            .setDisplay(Display.DIALOG)
            .start()
    }

    private fun initDrawer() {
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            rootLayout,
            toolbar,
            com.mikepenz.materialdrawer.R.string.material_drawer_open,
            com.mikepenz.materialdrawer.R.string.material_drawer_close
        )

        //initialize and create the image loader logic
        DrawerImageLoader.init(object : AbstractDrawerImageLoader() {
            override fun set(imageView: ImageView, uri: Uri, placeholder: Drawable, tag: String?) {
                Glide.with(imageView.context)
                    .load(uri)
                    .placeholder(R.drawable.ic_profile)
                    .centerCrop()
                    .into(imageView)
            }

            override fun cancel(imageView: ImageView) {
                Glide.with(imageView.context).clear(imageView)
            }

            override fun placeholder(ctx: Context, tag: String?): Drawable {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                return when (tag) {
                    DrawerImageLoader.Tags.PROFILE.name -> getPlaceHolder(ctx)
                    //we use the default one for
                    //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()
                    else -> super.placeholder(ctx, tag)
                }
            }
        })

        accountHeader = AccountHeaderView(this).apply {
            attachToSliderView(slider)
            selectionListEnabledForSingleProfile = false

            this.ivBack.setOnClickListener { rootLayout.closeDrawer(slider) }
        }

        val colorStateList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red))
//        val bg = ContextCompat.getDrawable(this, R.drawable.bg_bubble)
        val drawerWidth = ViewUtil.getScreenWidth(this) * 70 / 100
        slider.apply {
//            background = bg
            customWidth = drawerWidth

            addItems(
                PrimaryDrawerItem().apply {
                    identifier = DRAWER_HOME
                    iconRes = R.drawable.ic_drawer_home
                    name = StringHolder(R.string.all_home)
                },
                PrimaryDrawerItem().apply {
                    identifier = DRAWER_LOGOUT
                    iconRes = R.drawable.ic_drawer_logout
                    name = StringHolder(R.string.all_log_out)
                    isSelectable = false
                }
            )

            onDrawerItemClickListener = { v, item, position ->
                when (item.identifier) {
                    DRAWER_LOGOUT -> showLogOutDialog()
                }
                false
            }
        }
    }

    private fun bindUserData(account: Account) {
        // Add profile only once
        if (accountHeader.profiles?.isEmpty() == true) {
            accountHeader.addProfiles(
                ProfileDrawerItem().apply {
                    account.name?.also { nameText = it }
                    account.avatar?.also { iconUrl = it }
                    descriptionText = account.username.toString()
                }
            )
        }
    }

    override fun loadData() {
        if (!Connectivity.isConnected(this)) {
            ToastComposer.networkUnavailable(this)
            return
        }

        presenter.loadCollector(/*pageToLoad*/)
    }

    override fun endlessLoaderEnabled(): Boolean {
        return false
    }

    override val layoutManager: RecyclerView.LayoutManager?
        get() = LinearLayoutManager(this)

    override fun onCollectorLoaded(data: Collector) {
        if (isFinishing) return

        if (pageToLoad == 1) bindUserData(Account.getInstance())

        if (data.customers.isEmpty()) {
            showEmpty(getString(R.string.customers_empty))
            return
        }

//        if (adapter == null) {
            itemList = data.customers.toMutableList()
            adapter = CustomerAdapter(itemList, this).apply {
                setHasStableIds(true)
            }
            rvList.adapter = adapter
//            return
//        }

//        itemList.addAll(data.customers)
//        adapter!!.notifyDataSetChanged()

        emptyViewHolder?.hide()

        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                search_view.clearFocus()
                Log.i("Tag", "lalala")
                itemList = data.customers.toMutableList()
                presenter.searchCollector(query)
//                itemList.clear()
                rvList.adapter = adapter
                itemList.addAll(data.customers)

                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                if (query == "") {
                    loadData()
                }
                return true
            }

        })

    }

    override fun onItemClicked(v: View, data: Any?, position: Int) {
        val customer = data as Customer
        startActivityForResult(
            CustomerDetailActivity.newInstance(this, customer),
            RC_UPDATE_CUSTOMER
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_UPDATE_CUSTOMER && resultCode == Activity.RESULT_OK) {
            loadData()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showLogOutDialog() {
        DialogComposer.messageDialog(
            this,
            message = getString(R.string.all_log_out_confirmation),
            buttonText = getString(R.string.all_log_out),
            buttonListener = { doLogOut() }
        )
    }

    private fun doLogOut() {
        Account.getInstance().deleteCurrentUser()
        Settings.remove(Settings.Key.KEYS)

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        actionBarDrawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        actionBarDrawerToggle.syncState()
    }

    override fun onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (rootLayout.isDrawerOpen(slider)) {
            rootLayout.closeDrawer(slider)
        } else super.onBackPressed()
    }

    companion object {
        private const val DRAWER_HOME = 1L
        private const val DRAWER_LOGOUT = 2L
    }
}
