package com.softnesia.colmitra.ui.customer

import com.softnesia.colmitra.model.customer.CustomerRepository
import com.softnesia.colmitra.model.customer.CustomerResponse
import com.softnesia.colmitra.ui.BasePresenter
import com.softnesia.colmitra.util.network.ResponseCallback

class CustomerDetailPresenter(val view: CustomerContract.CustomerDetailView) : BasePresenter(),
    CustomerContract.CustomerDetailAction {
    override fun getCustomerDetail(customerId: Long) {
        view.setProgressIndicator(true)
        val call =
            CustomerRepository.detail(customerId, object : ResponseCallback<CustomerResponse> {
                override fun onSuccess(data: CustomerResponse) {
                    view.setProgressIndicator(false)
                    view.onCustomerLoaded(data)
                }

                override fun onFailed(message: String?) {
                    view.setProgressIndicator(false)
                    view.showError(message)
                }

                override fun onEmpty(message: String?) {}
            })
        addAsCancellableCall(call)
    }

    override fun submitUpdate(body: HashMap<String, Any>) {
        CustomerRepository.updateStatus(body, object : ResponseCallback<String?> {
            override fun onSuccess(data: String?) {
                view.setProgressIndicator(false)
                view.onSubmitSucceed(data)
            }

            override fun onFailed(message: String?) {
                view.setProgressIndicator(false)
                view.showError(message)
            }

            override fun onEmpty(message: String?) {}
        })
    }
}