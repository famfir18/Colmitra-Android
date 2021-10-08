package com.softnesia.colmitra.ui

import retrofit2.Call

abstract class BasePresenter {
    // List of calls to cancel on onDestroy
    private val cancellableCalls = arrayListOf<Call<*>>()

    protected fun addAsCancellableCall(call: Call<*>) {
        cancellableCalls.add(call)
    }

    fun onDestroy() {
        for (call in cancellableCalls) {
            call.cancel()
        }

        cancellableCalls.clear()
    }
}
