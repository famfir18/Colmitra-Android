package com.softnesia.colmitra.ui.main

import com.softnesia.colmitra.model.Collector
import com.softnesia.colmitra.model.CollectorRepository
import com.softnesia.colmitra.model.CollectorSearch
import com.softnesia.colmitra.ui.BasePresenter
import com.softnesia.colmitra.util.network.ResponseCallback

class MainPresenter(val view: MainContract.MainView) : BasePresenter(), MainContract.MainAction {
    override fun loadCollector(/*page: Int*/) {
        view.setProgressIndicator(true)
        val call = CollectorRepository.detail(object : ResponseCallback<Collector> {
            override fun onSuccess(data: Collector) {
                view.setProgressIndicator(false)
                view.onCollectorLoaded(data)
            }

            override fun onFailed(message: String?) {
                view.setProgressIndicator(false)
                view.showError(message)
            }

            override fun onEmpty(message: String?) {
                view.setProgressIndicator(false)
                view.showEmpty(message)
            }
        })
        addAsCancellableCall(call)
    }

    override fun searchCollector(search: String) {
        view.setProgressIndicator(true)
        val call = CollectorSearch.detail(search, object : ResponseCallback<Collector> {
            override fun onSuccess(data: Collector) {
                view.setProgressIndicator(false)
                view.onSearchCustomer(data)
            }

            override fun onFailed(message: String?) {
                view.setProgressIndicator(false)
                view.showError(message)
            }

            override fun onEmpty(message: String?) {
                view.setProgressIndicator(false)
                view.showEmpty(message)
            }
        })
        addAsCancellableCall(call)
    }
}