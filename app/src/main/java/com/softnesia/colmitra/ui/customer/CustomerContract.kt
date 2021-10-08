package com.softnesia.colmitra.ui.customer

import com.softnesia.colmitra.model.customer.CustomerResponse
import com.softnesia.colmitra.ui.BaseContract

/**
 * Created by Dark on 21/01/2018.
 */
class CustomerContract {
    interface CustomerDetailView : BaseContract.RemoteView {
        fun onCustomerLoaded(data: CustomerResponse)

        fun onSubmitSucceed(message: String?)
    }

    interface CustomerDetailAction {
        fun getCustomerDetail(customerId: Long)

        fun submitUpdate(body: HashMap<String, Any>)
    }
}