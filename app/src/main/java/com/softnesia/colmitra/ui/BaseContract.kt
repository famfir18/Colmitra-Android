package com.softnesia.colmitra.ui

/**
 * Created by Dark on 06/06/2016.
 */
class BaseContract {
    interface RemoteView {
        fun setProgressIndicator(active: Boolean)

        fun showError(message: String?)
    }

    interface RemoteListView : RemoteView {
        fun showEmpty(message: String?)
    }
}
