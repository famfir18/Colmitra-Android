package com.softnesia.colmitra.ui.customer

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import com.asksira.bsimagepicker.BSImagePicker
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
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

    private var idMitra: Int? = null

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
                        val valuePayment = parent?.selectedItem as PaymentStatus
                        etCustomerPaymentStatus.setText(valuePayment.name)

                        tilCustomerPtpDate.visibility = if (valuePayment.id == STATUS_PTP_ID) View.VISIBLE
                        else View.GONE
                    }
                    spinnerPaymentCalledOnce = true
                }
            }

//        spCustomerVisit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//            }
//
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                if (spinnerVisitCalledOnce) {
//
//                    val valueVisit = parent?.selectedItem as VisitStatus
//                    if (valueVisit.id == 1L) {
//                        spCustomerPaymentStatus.visibility = View.VISIBLE
//                        tilCustomerPaymentStatus.visibility = View.VISIBLE
//
//
//
//                    } else if (valueVisit.id == 2L) {
//                        spCustomerPaymentStatus.visibility = View.VISIBLE
//                        tilCustomerPaymentStatus.visibility = View.VISIBLE
//                    }
//                    /*spCustomerPaymentStatus.visibility = if (valueVisit.id == STATUS_VISIT_ID) View.VISIBLE
//                    else if (valueVisit.id == 2L) View.VISIBLE
//                    else View.GONE
//                    tilCustomerPaymentStatus.visibility = if (valueVisit.id == STATUS_VISIT_ID) View.VISIBLE
//                    else View.GONE*/
//
//                    val value = parent.selectedItem.toString()
//                    etCustomerVisit.setText(value)
//                }
//                spinnerVisitCalledOnce = true
//            }
//        }

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

        idMitra = customer.idMitra
        Log.i("TAG", "ID Mitra " + idMitra)

        etCustomerPhone.setOnClickListener { dialPhone(customer.phone) }
//        etCustomerCpPhone.setOnClickListener { dialPhone(customer.cpPhone) }
//        etCustomerOfficePhone.setOnClickListener { dialPhone(customer.officePhone) }

        etCustomerBCA.setText(customer.vabca)
        etCustomerMandiri.setText(customer.vaMandiri)
        etCustomerPermata.setText(customer.vaPermata)
        etCustomerDPD.setText(customer.dpd.toString())
        etMitra.setText(customer.mitra)
        etCustomerUserId.setText(customer.userId)
        etCustomerName.setText(customer.name)
        etCustomerPhone.setText(customer.phone)
        etCustomerCpPhone.setText(customer.cpPhone)
        etCustomerOfficePhone.setText(customer.officePhone)
        etCustomerAddress.setText(customer.address)
        etCustomerOfficeAddress.setText(customer.officeAddress)
        etCustomerBill.setText(LNumber.thousandDotSeparator(customer.billNominal))

        etCustomerBCA.setOnClickListener {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val vaBCA = etCustomerBCA.text
            val clipData = ClipData.newPlainText("text", vaBCA)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(this, "VA BCA berhasil disalin", Toast.LENGTH_SHORT).show()
        }

        etCustomerMandiri.setOnClickListener {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val vaMandiri = etCustomerMandiri.text
            val clipData = ClipData.newPlainText("text", vaMandiri)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(this, "VA Mandiri berhasil disalin", Toast.LENGTH_SHORT).show()
        }

        etCustomerPermata.setOnClickListener {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val vaPermata = etCustomerPermata.text
            val clipData = ClipData.newPlainText("text", vaPermata)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(this, "VA Permata berhasil disalin", Toast.LENGTH_SHORT).show()
        }

        etCustomerUserId.setOnClickListener {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val userID = etCustomerUserId.text
            val clipData = ClipData.newPlainText("text", userID)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(this, "ID Borrower berhasil disalin", Toast.LENGTH_SHORT).show()
        }

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

        fun statusBayar() {

            val bayarFull = response.paymentStatuses[0]
            val bayarSebagian = response.paymentStatuses[1]

            val payment = mutableListOf<PaymentStatus>()

            payment.add(bayarFull)
            payment.add(bayarSebagian)

            val paymentAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                payment
            )
            response.paymentStatuses[0]

            val gson = Gson()
            spCustomerPaymentStatus.adapter = paymentAdapter

        }

        fun statusPTP() {
            val ptp = response.paymentStatuses[2]
            val payment = mutableListOf<PaymentStatus>()
            payment.add(ptp)

            val paymentAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                payment
            )
            response.paymentStatuses[0]

            val gson = Gson()
            spCustomerPaymentStatus.adapter = paymentAdapter

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

                    val valueVisit = parent?.selectedItem as VisitStatus
                    if (valueVisit.id == 1L) {
                        spCustomerPaymentStatus.visibility = View.VISIBLE
                        tilCustomerPaymentStatus.visibility = View.VISIBLE
                        tilCustomerAmcoll.visibility = View.VISIBLE
                        tilCustomerPtpDate.visibility = View.GONE
                        statusBayar()

                    } else if (valueVisit.id == 3L) {
                        spCustomerPaymentStatus.visibility = View.GONE
                        tilCustomerPaymentStatus.visibility = View.GONE
                        tilCustomerAmcoll.visibility = View.GONE
                        tilCustomerPtpDate.visibility = View.GONE

                    } else if (valueVisit.id == 2L) {
                        spCustomerPaymentStatus.visibility = View.VISIBLE
                        tilCustomerPaymentStatus.visibility = View.VISIBLE
                        tilCustomerAmcoll.visibility = View.GONE
                        statusPTP()
                    }

                    val value = parent.selectedItem.toString()
                    etCustomerVisit.setText(value)
                }
                spinnerVisitCalledOnce = true
            }
        }



        /*customer.paymentStatus?.also {
            response.paymentStatuses.forEachIndexed { index, status ->
                if (status.id == it) {
                    etCustomerPaymentStatus.setText(status.name)
                    spCustomerPaymentStatus.setSelection(index, false)
                    return@forEachIndexed
                }
            }
        }*/

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
            "id_login_collector" to Account.getInstance().id,
            "id_nasabah" to customer.id,
            "id_mitra" to idMitra.toString(),
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

        map["status_bayar"] = etCustomerPaymentStatus.text?.let {
            if (it.isNotEmpty()) {
                val paymentStatus = spCustomerPaymentStatus.selectedItem as PaymentStatus
                paymentStatus.id.toString()
            } else ""
        } ?: ""

        map["status_visit"] = etCustomerVisit.text?.let {
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