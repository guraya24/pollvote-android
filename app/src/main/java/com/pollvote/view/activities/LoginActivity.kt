package com.pollvote.view.activities


import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.hbb20.CountryCodePicker
import com.pollvote.R
import com.pollvote.annotations.Status
import com.pollvote.model.login.LoginResponse
import com.pollvote.network.Constant
import com.pollvote.utils.ProgressUtils
import com.pollvote.utils.sharedPreference.SharedPrefrencesUtils
import com.pollvote.utils.Static
import com.pollvote.viewModel.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.et_password
import kotlinx.android.synthetic.main.selection_activity.btn_login

class LoginActivity : BaseActivity() {
    var isRemember: Boolean = false
    private lateinit var viewModel: LoginViewModel
    var mobile = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // initialize view model
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        // initialize observer
        initObserver()

        // fetch token
        getFcmToken()

        val setToolbarTitle = Static()
        setToolbarTitle.statusBarColor(this)
        initUI()
    }

    // initiate UI
    fun initUI() {

        ll_remember.setOnClickListener {
            if (!isRemember) {
                isRemember = true
                img_remember.setImageResource(R.drawable.tick_filled)
            } else {
                isRemember = false
                img_remember.setImageResource(R.drawable.untick_remeber)
            }
        }


        if (SharedPrefrencesUtils.getUserID() != "") {
            et_user_id.setText(SharedPrefrencesUtils.getUserID().toString())
            et_password.setText(SharedPrefrencesUtils.getUserPassword().toString())
        } else {
            et_user_id.setText("")
            et_password.setText("")
        }

        if (intent.hasExtra(Constant.mobile)) {
            et_user_id.setText(intent.getStringExtra(Constant.mobile))
        }

        /*Mobile number format */
        et_user_id.addTextChangedListener(PhoneNumberFormattingTextWatcher("US"))
        val cCPCountry = CountryCodePicker(this)
//        cCPCountry.setAutoDetectedCountry(true)
        cCPCountry.setDefaultCountryUsingNameCode("US")
        cCPCountry.setCountryForPhoneCode(1)
        cCPCountry.registerCarrierNumberEditText(et_user_id)

        btn_login.setOnClickListener {
            if (et_user_id.text.toString().isEmpty()) {
                et_user_id.error = getString(R.string.enter_mobile)
            }
            /*else if (!cCPCountry.isValidFullNumber) {
                et_user_id.error = getString(R.string.enter_valid_mobile)
            } */
            else if (et_password.text.toString().isEmpty()) {
                et_password.error = getString(R.string.enter_password)
            } else {
                val mobile = et_user_id.text.toString().trim()
                val password = et_password.text.toString().trim()

//                // call login API
//                viewModel.callLogin(
//                    mobile,
//                    password,
//                    SharedPrefrencesUtils.getDeviceId(),
////                    SharedPrefrencesUtils.getFcmToken().toString()
//                )
//            }
            // call login API
            viewModel.callLogin(
                mobile,
                password,
                "64AE3AC8-DCD7-40AF-B6EF-C75257F9B359",
                "Android","fNuFHzacHE6FsmWV_uPGFj:APA91bEaqLjgDBflsUo2PGJyBs4VP8jbAygmf1OYZLNztoTxb0kHk3QWHoHA3gdUsMK736rlKk898rcECfdcMk0Z2Y96_LUM_bpFhjJDZGg77ojgYLf8Mgq1xiyN6P6kCKxpMDhT0Uua"
//                    SharedPrefrencesUtils.getFcmToken().toString()
            )
        }

        }

        txt_forgot_password.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }
//        et_user_id.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                if (et_user_id.text.toString().length == 12) {
//                    img_mail_id_correct.visibility = View.VISIBLE
//                } else {
//                    img_mail_id_correct.visibility = View.GONE
//                }
//
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//            }
//        })
    }


    // init Observer to observe API call initiate and result.
    private fun initObserver() {
        viewModel.isLoading.observe(this,
            Observer {
                if (it) {
                    ProgressUtils.showLoadingDialog(this)
                } else {
                    ProgressUtils.cancelLoading()
                }
            })

        viewModel.resultData.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    val dataLogin = it?.data?.data!!

                    it.data?.token?.let { it1 ->
                        SharedPrefrencesUtils.setUserLogin(
                            true,
                            dataLogin.id.toString(),
                            dataLogin.firstName,
                            dataLogin.email,
                            dataLogin.mobileNo,
                            dataLogin.drivingLicense,
                            dataLogin.voterId,
                            dataLogin.address?.addressLine1 + ", " + dataLogin.address?.twonShip,

                            it1

                        )
                    }

                    if (!isRemember) {
                        SharedPrefrencesUtils.setUserID("")
                        SharedPrefrencesUtils.setUserPassword("")
                    } else {
                        SharedPrefrencesUtils.setUserID(et_user_id.text.toString())
                        SharedPrefrencesUtils.setUserPassword(et_password.text.toString())

                    }

                    if (dataLogin.isAddressAvailable) {
                        startActivity(Intent(this@LoginActivity, EventListActivity::class.java))
                        finishAffinity()
                    } else {
                        startActivity(
                            Intent(this@LoginActivity, FillDetailActivity::class.java)
                                .putExtra("isLogin", true)
                        )
                        finishAffinity()
                    }
                }
                Status.FAILURE -> {
                    makeToast("" + it.errorMsg)
                }
            }
        })
    }

    // get fcm token from service
    private fun getFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            token?.let {
                SharedPrefrencesUtils.setFcmToken(it)
            }
        })
    }
}