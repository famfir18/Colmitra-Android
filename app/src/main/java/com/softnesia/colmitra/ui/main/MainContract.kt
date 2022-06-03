package com.softnesia.colmitra.ui.main

import com.softnesia.colmitra.model.Collector
import com.softnesia.colmitra.ui.BaseContract

/**
 * Created by Dark on 21/01/2018.
 */
class MainContract {
    interface MainView : BaseContract.RemoteListView {
        fun onCollectorLoaded(data: Collector)
        fun onSearchCustomer(data: Collector)
    }

    interface MainAction {
        fun loadCollector(/*page: Int*/)
        fun searchCollector(search: String)
    }
}