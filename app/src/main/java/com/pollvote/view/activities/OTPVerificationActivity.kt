package com.pollvote.view.activities

import `in`.aabhasjindal.otptextview.OtpTextView
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pollvote.R
import com.pollvote.annotations.Status
import com.pollvote.utils.ProgressUtils
import com.pollvote.utils.sharedPreference.SharedPrefrencesUtils
import com.pollvote.utils.Static
import com.pollvote.network.Constant
import com.pollvote.viewModel.SignUpViewModel
import kotlinx.android.synthetic.main.activity_otp_verification.*
import kotlinx.android.synthetic.main.layout_go_vote.*


class OTPVerificationActivity : BaseActivity() {
    private lateinit var otpTextView: OtpTextView;
    private lateinit var viewModel: SignUpViewModel
    var mobileNo = ""
    var deviceId = ""
    var expireTime: Double = 45000.0
    var resendType = ""
    var requestId: Int = 0
    var isContinue = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)
        // initialize view model
        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)

        // initialize observer
        initObserver()

        val setToolbarTitle = Static()
        setToolbarTitle.statusBarColor(this)
        initUI()
        countDownTimer()

    }

    // initiate UI and data from intent
    private fun initUI() {
        otpTextView = findViewById(R.id.otp_view)

        mobileNo = intent.getStringExtra(Constant.mobile).toString()
        deviceId = SharedPrefrencesUtils.getDeviceId().toString()
        resendType = intent.getStringExtra(Constant.ResendType).toString()
        expireTime = intent.getDoubleExtra(Constant.ExpireIn, 0.0)
        requestId = intent.getIntExtra(Constant.RequestId, 0)

        txt_resend.setTextColor(ContextCompat.getColor(this,R.color.grey))
        txt_resend.isClickable = false
        // data from intent
        val isMobileChanged = intent.getBooleanExtra(Constant.isMobileChanged, false);
        val isForgotPassword = intent.getBooleanExtra("isForgotPassword", false)

        btn_continue.setOnClickListener {
            if (isContinue) {
                if (isMobileChanged) {
                    if (otp_view.otp?.length == 6) {
                        val newMobile = intent.getStringExtra(Constant.newMobile).toString()
                        val oldMobile = intent.getStringExtra(Constant.oldMobile).toString()

                        viewModel.getMobileVerify(
                            oldMobile,
                            newMobile,
                            otp_view.otp!!.toString(),
                            SharedPrefrencesUtils.getToken().toString()
                        )

                    } else {
                        btn_continue.startAnimation(
                            AnimationUtils.loadAnimation(
                                this,
                                R.anim.shaking
                            )
                        )

                        makeToast(getString(R.string.please_enter_six_digit))
                    }

                } else if (isForgotPassword) {
                    if (otp_view.otp?.length == 6) {
                        startActivity(Intent(this, AddNewPasswordActivity::class.java))
                        makeToast(getString(R.string.your_number_verified))
                    } else {
                        btn_continue.startAnimation(
                            AnimationUtils.loadAnimation(
                                this,
                                R.anim.shaking
                            )
                        )

                        makeToast(getString(R.string.please_enter_six_digit))
                    }
                } else {
                    if (otp_view.otp?.length == 6) {
                        // call verify OTP if user entered 6 digits
                        viewModel.getOTPVerify(
                            mobileNo,
                            deviceId,
                            otp_view.otp!!.toString()
                        )
                    } else {
                        btn_continue.startAnimation(
                            AnimationUtils.loadAnimation(
                                this,
                                R.anim.shaking
                            )
                        )
                        btn_continue.startAnimation(
                            AnimationUtils.loadAnimation(
                                this,
                                R.anim.shaking
                            )
                        )
                        makeToast(getString(R.string.please_enter_six_digit))
                    }

                }
            } else {
                btn_continue.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shaking))
            }
        }
        img_back.setOnClickListener {
            finish()
        }

        txt_resend.setOnClickListener {

            if (!isContinue) {
                viewModel.getResendOTPVerify(
                    mobileNo,
                    deviceId,
                    resendType,
                    requestId
                )
            } else {

            }


        }
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

        viewModel.resultOTPData.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {

                    val dataa = it.data?.data
                    SharedPrefrencesUtils.setUserLogin(
                        true,
                        dataa?.id.toString(),
                        dataa?.firstName,
                        dataa?.mobileNo.toString(),
                        dataa?.drivingLicense.toString(),
                        dataa?.voterId.toString(),
                        it.data?.token.toString()
                    )
                    SharedPrefrencesUtils.clearUserRemember()
                    startActivity(
                        Intent(
                            this@OTPVerificationActivity,
                            FillDetailActivity::class.java
                        )
                    )
                    finish()
                }

                Status.FAILURE -> {
                    it.errorMsg?.let { it1 -> makeToast(it1) }
                }
            }

        })
        viewModel.resultResendOTPData.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {

                    val dataa = it.data?.data

                    txt_resend.setTextColor(ContextCompat.getColor(this,R.color.grey))
                    txt_resend.isClickable = false
                    // btn_continue.isClickable = true
                    isContinue = true
                    expireTime = dataa?.expireIn!!

                    countDownTimer()
                    makeToast(getString(R.string.otp_resent))

                    txt_otp.text = getString(R.string.code_expires_in)
                    txt_otp.setTextColor(ContextCompat.getColor(this,R.color.black))

                    btn_continue.setBackgroundResource(R.drawable.bg_button)
                    btn_continue.setTextColor(ContextCompat.getColor(this,R.color.white))
                }

                Status.FAILURE -> {
                    it.errorMsg?.let { it1 -> makeToast(it1) }
                }
            }

        })

        viewModel.resultChangeMobileNoData.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.message?.let { it1 -> makeToast(it1) }
                    finish()
                }

                Status.FAILURE -> {
                    it.errorMsg?.let { it1 -> makeToast(it1) }
                }
            }
        })
    }

    // count down timer for resend otp
    private fun countDownTimer() {
        object : CountDownTimer(expireTime.toLong(), 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                txt_timer.text = "" + millisUntilFinished / 1000 + " " + getString(R.string.seconds)
            }

            override fun onFinish() {
                isContinue = false
                txt_timer.text = ""
                txt_otp.text = getString(R.string.otp_expired)
                txt_otp.setTextColor(ContextCompat.getColor(this@OTPVerificationActivity,R.color.red))

                txt_resend.setTextColor(ContextCompat.getColor(this@OTPVerificationActivity,R.color.black))
                txt_resend.isClickable = true

                btn_continue.setBackgroundResource(R.drawable.bg_button_gray)
                btn_continue.setTextColor(ContextCompat.getColor(this@OTPVerificationActivity,R.color.black))
                //btn_continue.isClickable = false
            }
        }.start()

    }

}