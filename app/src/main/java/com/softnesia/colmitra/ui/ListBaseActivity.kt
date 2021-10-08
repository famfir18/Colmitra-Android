package com.jrtracking.surveyor.ui

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.softnesia.colmitra.R
import com.softnesia.colmitra.ui.BaseActivity
import com.softnesia.colmitra.ui.BaseContract
import com.softnesia.colmitra.ui.ListBaseAdapter
import com.softnesia.colmitra.ui.widget.list.EmptyViewHolder
import com.softnesia.colmitra.ui.widget.list.EndlessRecyclerViewOnScrollListener
import com.softnesia.colmitra.ui.widget.message.ToastComposer

abstract class ListBaseActivity : BaseActivity(), BaseContract.RemoteListView, OnRefreshListener {
    private val PARAM_PAGE = "page"

    protected var srlRefresh: SwipeRefreshLayout? = null
    protected lateinit var rvList: RecyclerView
    protected var emptyViewHolder: EmptyViewHolder? = null
    protected var scrollListener: EndlessRecyclerViewOnScrollListener? = null

    protected var pageToLoad = 1
    protected var adapter: ListBaseAdapter<*>? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageToLoad = savedInstanceState?.getInt(PARAM_PAGE) ?: 1
    }

    override fun onStart() {
        super.onStart()

        srlRefresh?.setColorSchemeResources(R.color.colorAccent)
        srlRefresh?.setOnRefreshListener(this)

        if (rvList.layoutManager == null) rvList.layoutManager = layoutManager

        if (endlessLoaderEnabled() && scrollListener == null) {
            initScrollListener()
        }

        emptyViewHolder?.also {
            if (it.clickListener == null) {
                it.buttonClickListener(View.OnClickListener { _ ->
                    it.hide()
                    loadData()
                })
            }
        }
    }

    override fun onRefresh() {
        pageToLoad = 1
        adapter = null

        if (endlessLoaderEnabled()) {
            rvList.removeOnScrollListener(scrollListener!!)
            initScrollListener()
        }

        loadData()
    }

    override fun setProgressIndicator(active: Boolean) {
        srlRefresh?.isRefreshing = active
    }

    protected fun showConnectionError() {
        if (pageToLoad == 1) {
            emptyViewHolder?.let {
                it.setMessage(getString(R.string.error_network_unavailable))
                it.show()
            } ?: ToastComposer.networkUnavailable(this)

            rvList.visibility = View.GONE
        } else ToastComposer.networkUnavailable(this)

        setProgressIndicator(false)
    }

    override fun showEmpty(message: String?) {
        if (isFinishing || pageToLoad > 1) return

        val text = message ?: emptyText()

        emptyViewHolder?.let {
            it.setMessage(text)
            it.show()
        } ?: ToastComposer.dataEmpty(this, text)

        rvList.visibility = View.GONE
    }

    override fun showError(message: String?) {
        if (isFinishing) return

        val text = message ?: errorText()

        emptyViewHolder?.let {
            it.setMessage(text)
            it.show()
        } ?: ToastComposer.loadError(this, text)

        rvList.visibility = View.GONE
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(PARAM_PAGE, pageToLoad)
    }

    protected fun initEmptyView(v: View?): EmptyViewHolder? {
        return v?.let {
            emptyViewHolder = EmptyViewHolder(v)
            emptyViewHolder
        }
    }

    private fun initScrollListener() {
        scrollListener = object : EndlessRecyclerViewOnScrollListener(rvList.layoutManager!!) {
            override fun onLoadMore(page: Int, totalItemsCount: Int) {
                pageToLoad = page + 1
                loadData()
            }
        }.also { rvList.addOnScrollListener(it) }
    }

    open fun emptyText(): String {
        return getString(R.string.error_data_empty)
    }

    open fun errorText(): String {
        return getString(R.string.error_loading_failed)
    }

    abstract fun loadData()
    protected abstract fun endlessLoaderEnabled(): Boolean
    protected abstract val layoutManager: RecyclerView.LayoutManager?
}