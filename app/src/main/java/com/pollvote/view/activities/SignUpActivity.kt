package com.pollvote.view.activities

import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.widget.Button
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.hbb20.CountryCodePicker
import com.pollvote.R
import com.pollvote.annotations.Status
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.et_password
import com.pollvote.model.login.UserSignupData
import com.pollvote.network.Constant
import com.pollvote.utils.ProgressUtils
import com.pollvote.utils.sharedPreference.SharedPrefrencesUtils
import com.pollvote.utils.Static
import com.pollvote.viewModel.SignUpViewModel
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Pattern

//Sign up with basic details
class SignUpActivity : BaseActivity() {

    lateinit var btn_continue: Button
    lateinit var img_back: ImageView
    val static = Static()
    lateinit var cCPCountry: CountryCodePicker
    private lateinit var viewModel: SignUpViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // initialize view model
        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)

        // initialize observer
        initObserver()


        getFcmToken()
        val setToolbarTitle = Static()
        setToolbarTitle.statusBarColor(this)

        initUI()

    }

    //initiate UI and data from intent
    private fun initUI() {
        et_mobile_number.setText(intent.getStringExtra(Constant.mobile))

        img_back = findViewById(R.id.img_back)
        btn_continue = findViewById(R.id.btn_continue)

        et_mobile_number.addTextChangedListener(PhoneNumberFormattingTextWatcher("US"))
        cCPCountry = CountryCodePicker(this)
//        cCPCountry.setAutoDetectedCountry(true)
        cCPCountry.setDefaultCountryUsingNameCode("US")
        cCPCountry.setCountryForPhoneCode(1)
        cCPCountry.registerCarrierNumberEditText(et_mobile_number)

        if (intent.hasExtra("data")) {
            val data = intent.getSerializableExtra("data") as UserSignupData
            et_first_name.setText(data.firstName)
            et_dl_number.setText(data.drivingLicence)
            et_voter_id.setText(data.voterId)
            et_email.setText(data.email)
        }

        btn_continue.setOnClickListener {
            if (isValid()) {
                val deviceId = SharedPrefrencesUtils.getDeviceId().toString()
                val fcmToken = SharedPrefrencesUtils.getFcmToken().toString()
                val deviceType = SharedPrefrencesUtils.DeviceType

                // call signup API using view model
                viewModel.callSignUp(
                    et_first_name.text.toString(),
                    et_dl_number.text.toString(),
                    et_mobile_number.text.toString(),
                    et_email.text.toString(),
                    et_password.text.toString(),
                    deviceId,
                    deviceType,
                    fcmToken,
                    et_voter_id.text.toString()
                )
            }
        }

        img_back.setOnClickListener {
            finish()
        }
    }

    //check validations
    private fun isValid(): Boolean {
        var is_valid = true
        if (et_first_name.text.toString() == "") {
            et_first_name.error = getString(R.string.enter_first_name)
            is_valid = false
        } else if (et_first_name.text.toString().split(" ").size == 1) {
            et_first_name.error = getString(R.string.enter_full_name)
            is_valid = false
        } else if (et_first_name.text.toString()
                .split(" ").size == 2 && et_first_name.text.toString()
                .split(" ")[1].isEmpty()
        ) {
            et_first_name.error = getString(R.string.enter_full_name)
            is_valid = false
        }
        if (et_dl_number.text.toString() == "") {
            et_dl_number.error = getString(R.string.enter_driving_lic)
            is_valid = false
        }
        if (et_voter_id.text.toString() == "") {
            et_voter_id.error = getString(R.string.enter_voter_id)
            is_valid = false
        }
        if (et_mobile_number.text.toString() == "") {
            et_mobile_number.error = getString(R.string.enter_mobile)
            is_valid = false
        } else if (!cCPCountry.isValidFullNumber) {
            et_mobile_number.error = getString(R.string.enter_valid_mobile)
            is_valid = false
        }
        if (et_email.text.toString() == "") {
            et_email.error = getString(R.string.enter_email)
            is_valid = false
        }
        if (!static.isValidEmail(et_email.text.toString())) {
            et_email.error = getString(R.string.enter_valid_email)
            is_valid = false
        }
        if (et_password.text.toString() == "") {
            et_password.error = getString(R.string.enter_password)
            is_valid = false
        } else if (et_password.text.toString().length<8) {
            et_password.error = getString(R.string.enter_password_length)
            is_valid = false
        }
        return is_valid
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

                    startActivity(
                        Intent(
                            this@SignUpActivity,
                            OTPVerificationActivity::class.java
                        )
                            .putExtra(Constant.mobile, it.data?.data?.mobileNumber)
                            .putExtra(Constant.ExpireIn, it.data?.data?.expireIn)
                            .putExtra(Constant.RequestId, it.data?.data?.requestId)
                            .putExtra(Constant.ResendType, it.data?.data?.resendType)
                    )
                    finish()
                    it.data?.message?.let { it1 -> makeToast(it1) }


                }

                Status.FAILURE -> {
                    it.errorMsg?.let { it1 -> makeToast(it1) }
                }
            }
        })
    }

    // validation for password
    private fun isValidPassword(): Boolean {
        val str = et_password.getText()
        var valid = true

        // Password policy check
        // Password should be minimum minimum 8 characters long
        if (str?.length!! < 8) {
            valid = false
        }
        // Password should contain at least one number
        var exp = ".*[0-9].*"
        var pattern = Pattern.compile(exp, Pattern.CASE_INSENSITIVE)
        var matcher = pattern.matcher(str)
        if (!matcher.matches()) {
            valid = false
        }

        // Password should contain at least one capital letter
        exp = ".*[A-Z].*"
        pattern = Pattern.compile(exp)
        matcher = pattern.matcher(str)
        if (!matcher.matches()) {
            valid = false
        }

        // Password should contain at least one small letter
        exp = ".*[a-z].*"
        pattern = Pattern.compile(exp)
        matcher = pattern.matcher(str)
        if (!matcher.matches()) {
            valid = false
        }

        // Password should contain at least one special character
        // Allowed special characters : "~!@#$%^&*()-_=+|/,."';:{}[]<>?"
        exp = ".*[~!@#\$%\\^&*()\\-_=+\\|\\[{\\]};:'\",<.>/?].*"
        pattern = Pattern.compile(exp)
        matcher = pattern.matcher(str)
        if (!matcher.matches()) {
            valid = false
        }

        // Set error if required

        val error: String? =
            if (valid) null else getString(R.string.password_policy)
        et_password.error = error


        return valid
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