package com.softnesia.colmitra.ui.customer

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.asksira.bsimagepicker.BSImagePicker
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.softnesia.colmitra.IMAGE_FILE_PROVIDER
import com.softnesia.colmitra.PHOTO_MAX_WIDTH
import com.softnesia.colmitra.R
import com.softnesia.colmitra.model.account.Account
import com.softnesia.colmitra.model.customer.*
import com.softnesia.colmitra.radius
import com.softnesia.colmitra.ui.BaseActivity
import com.softnesia.colmitra.ui.widget.message.DialogComposer
import com.softnesia.colmitra.ui.widget.message.ToastComposer
import com.softnesia.colmitra.util.*
import com.softnesia.colmitra.util.network.Connectivity
import kotlinx.android.synthetic.main.activity_customer_detail.*
import kotlinx.android.synthetic.main.toolbar.*

class CustomerDetailActivity : BaseActivity(),
    CustomerContract.CustomerDetailView,
    BSImagePicker.OnSingleImageSelectedListener,
    BSImagePicker.ImageLoaderDelegate {

    private lateinit var presenter: CustomerDetailPresenter

    private lateinit var customer: Customer
    private var lat: Double = DEFAULT_LAT
    private var lng: Double = DEFAULT_LNG
    private var ptpDate: String? = null
    private var photoUri: Uri? = null

    private var spinnerPaymentCalledOnce = false
    private var spinnerVisitCalledOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_detail)

        rootLayout.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        setToolbarNoTitle(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        etCustomerPaymentStatus.setOnClickListener { spCustomerPaymentStatus.performClick() }
        etCustomerVisit.setOnClickListener { spCustomerVisit.performClick() }
        ivCamera.setOnClickListener {
            BSImagePicker.Builder(IMAGE_FILE_PROVIDER)
                .build()
                .show(supportFragmentManager, "image_picker")
        }
        btnSubmit.setOnClickListener { submitUpdate() }

        spCustomerPaymentStatus.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (spinnerPaymentCalledOnce) {
                        val value = parent?.selectedItem as PaymentStatus
                        etCustomerPaymentStatus.setText(value.name)

                        tilCustomerPtpDate.visibility = if (value.id == STATUS_PTP_ID) View.VISIBLE
                        else View.GONE
                    }
                    spinnerPaymentCalledOnce = true
                }
            }

        spCustomerVisit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (spinnerVisitCalledOnce) {
                    val value = parent?.selectedItem.toString()
                    etCustomerVisit.setText(value)
                }
                spinnerVisitCalledOnce = true
            }
        }

        etCustomerPtpDate.setOnClickListener {
            DialogComposer.displayDateDialog(
                this,
                ptpDate ?: "",
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    val value =
                        "$year-${LDate.digitFormat(month + 1)}-${LDate.digitFormat(dayOfMonth)}"
                    ptpDate = value
                    etCustomerPtpDate.setText(
                        LDate.indonesianDateFormat(
                            value,
                            LDate.defaultDateFormat(),
                            LDate.dateFormatddMMMMyyyy()
                        )
                    )
                }
            ).show()
        }

        customer = intent.getParcelableExtra(PARAM_CUSTOMER)!!

        presenter = CustomerDetailPresenter(this)
        basePresenter = presenter

        getLocation()
        loadData()
    }

    private fun loadData() {
        if (!Connectivity.isConnected(this)) {
            ToastComposer.networkUnavailable(this)
            finish()
            return
        }

        presenter.getCustomerDetail(customer.id)
    }

    private fun bindData(response: CustomerResponse) {
        val customer = response.customer

        etCustomerPhone.setOnClickListener { dialPhone(customer.phone) }
        etCustomerCpPhone.setOnClickListener { dialPhone(customer.cpPhone) }
        etCustomerOfficePhone.setOnClickListener { dialPhone(customer.officePhone) }

        etCustomerUserId.setText(customer.userId)
        etCustomerName.setText(customer.name)
        etCustomerPhone.setText(customer.phone)
        etCustomerCpPhone.setText(customer.cpPhone)
        etCustomerOfficePhone.setText(customer.officePhone)
        etCustomerAddress.setText(customer.address)
        etCustomerOfficeAddress.setText(customer.officeAddress)
        etCustomerBill.setText(LNumber.thousandDotSeparator(customer.billNominal))
        if (customer.amcoll > 0) etCustomerAmcoll.setText(LNumber.thousandDotSeparator(customer.amcoll))
        etCustomerNote.setText(customer.note)
        customer.photoUrl?.also {
            val options: RequestOptions = RequestOptions()
                .transform(MultiTransformation(CenterCrop(), RoundedCorners(this.radius)))

            Glide.with(ivPhoto)
                .load(it)
                .apply(options)
                .into(ivPhoto)

            tvPhotoError.visibility = View.GONE
        }

        val paymentAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            response.paymentStatuses
        )
        spCustomerPaymentStatus.adapter = paymentAdapter

        customer.paymentStatus?.also {
            response.paymentStatuses.forEachIndexed { index, status ->
                if (status.id == it) {
                    etCustomerPaymentStatus.setText(status.name)
                    spCustomerPaymentStatus.setSelection(index, false)
                    return@forEachIndexed
                }
            }
        }

        val visitAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            response.visitStatuses
        )
        spCustomerVisit.adapter = visitAdapter

        customer.visitStatus?.also {
            response.visitStatuses.forEachIndexed { index, status ->
                if (status.id == it) {
                    etCustomerVisit.setText(status.name)
                    spCustomerVisit.setSelection(index, false)
                    return@forEachIndexed
                }
            }
        }
    }

    private fun dialPhone(phone: String?) {
        phone?.also { IntentUtil.openDial(this, it) }
    }

    override fun onSingleImageSelected(uri: Uri?, tag: String?) {
        photoUri = uri

        val options: RequestOptions = RequestOptions()
            .transform(MultiTransformation(CenterCrop(), RoundedCorners(this.radius)))

        Glide.with(ivPhoto)
            .load(uri)
            .apply(options)
            .into(ivPhoto)

        tvPhotoError.visibility = View.GONE
    }

    override fun loadImage(imageUri: Uri?, ivImage: ImageView?) {
        Glide.with(ivImage!!)
            .load(imageUri)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(ivImage)
    }

    override fun onLocationChanged(location: Location) {
        lat = location.latitude
        lng = location.longitude
        locationManager.cancel()
    }

    private fun submitUpdate() {
        if (!Connectivity.isConnected(this)) {
            ToastComposer.networkUnavailable(this)
            return
        }

//        if (!isFormValid) {
//            ToastComposer.showErrorType(this, R.string.error_form_not_complete)
//            return
//        }

        ViewUtil.hideKeyboard(this)
        setProgressIndicator(true)

        val map = hashMapOf(
            "id_collection" to Account.getInstance().id,
            "id_nasabah" to customer.id,
            "catatan" to etCustomerNote.text.toString(),
            "lat" to lat,
            "lang" to lng
        )

        map["amcoll"] = etCustomerAmcoll.text?.let {
            LNumber.formatToNumber(
                it.toString(),
                LNumber.getDecimalFormatSymbols(true)
            )
        } ?: ""

        map["methode_pembayaran"] = etCustomerPaymentStatus.text?.let {
            if (it.isNotEmpty()) {
                val paymentStatus = spCustomerPaymentStatus.selectedItem as PaymentStatus
                paymentStatus.id.toString()
            } else ""
        } ?: ""

        map["status"] = etCustomerVisit.text?.let {
            if (it.isNotEmpty()) {
                val visitStatus = spCustomerVisit.selectedItem as VisitStatus
                visitStatus.id.toString()
            } else ""
        } ?: ""

        map["foto"] = photoUri?.let { uri ->
            val file = ImageUtil.decodeAsFile(this, uri, PHOTO_MAX_WIDTH)
            val base64photo: String = file?.let { ImageUtil.convertImageFileToBase64(it) }!!

            base64photo
        } ?: ""

        map["tanggal_ptp"] = ptpDate ?: ""

        presenter.submitUpdate(map)
    }

    private val isFormValid: Boolean
        get() {
            var isValid = true

            if (ValidationUtil.isTextEmpty(
                    tilCustomerPaymentStatus,
                    getString(R.string.customer_payment_status_error)
                )
            ) {
                isValid = false
            }

            // PTP date must be filled for PTP PaymentStatus
            val paymentStatus = spCustomerPaymentStatus.selectedItem as PaymentStatus
            if (paymentStatus.id == STATUS_PTP_ID && ptpDate.isNullOrEmpty()) {
                tilCustomerPtpDate.error = getString(R.string.error_must_not_empty)
                isValid = false
            }

            if (ValidationUtil.isTextEmpty(
                    tilCustomerAmcoll,
                    getString(R.string.error_must_not_empty)
                )
            ) {
                isValid = false
            }

            if (ValidationUtil.isTextEmpty(
                    tilCustomerVisit,
                    getString(R.string.customer_visit_error)
                )
            ) {
                isValid = false
            }

            return isValid
        }

    override fun setProgressIndicator(active: Boolean) {
        btnSubmit.visibility = if (active) View.GONE else View.VISIBLE
        progressBar.visibility = if (active) View.VISIBLE else View.GONE
    }

    override fun showError(message: String?) {
        if (isFinishing) return
        DialogComposer.messageDialog(
            this,
            message = message ?: getString(R.string.error_occurred),
            buttonText = "OK"
        )
    }

    override fun onCustomerLoaded(data: CustomerResponse) {
        if (isFinishing) return
        bindData(data)
    }

    override fun onSubmitSucceed(message: String?) {
        if (isFinishing) return
        ToastComposer.show(applicationContext, message)

        setResult(Activity.RESULT_OK)
        finish()
    }

    companion object {
        private const val PARAM_CUSTOMER = "customer"
        const val DEFAULT_LAT = -6.2180016
        const val DEFAULT_LNG = 106.8150181

        fun newInstance(context: Context, customer: Customer): Intent {
            val intent = Intent(context, CustomerDetailActivity::class.java)
            intent.putExtra(PARAM_CUSTOMER, customer)
            return intent
        }
    }
}