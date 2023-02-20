package com.pollvote.view.activities

import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hbb20.CountryCodePicker
import com.pollvote.R
import com.pollvote.annotations.Status
import com.pollvote.network.Constant
import com.pollvote.utils.ProgressUtils
import com.pollvote.utils.sharedPreference.SharedPrefrencesUtils
import com.pollvote.utils.Static
import com.pollvote.viewModel.UserProfileViewModel
import kotlinx.android.synthetic.main.activity_change_mobile_number.*
import kotlinx.android.synthetic.main.activity_change_mobile_number.img_back
import kotlinx.android.synthetic.main.activity_change_password.btn_continue


class ChangeMobileNumberActivity : BaseActivity() {
    var token = ""
    private lateinit var userProfileViewModel: UserProfileViewModel
    lateinit var cCPCountry: CountryCodePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_mobile_number)
        val setToolbarTitle = Static()
        setToolbarTitle.statusBarColor(this)
        token = SharedPrefrencesUtils.getToken().toString()
        userProfileViewModel = ViewModelProvider(this).get(UserProfileViewModel::class.java)

        initiUI()
        initChangeMobileObserver()
    }

    private fun initiUI() {
        et_new_mobile.addTextChangedListener(PhoneNumberFormattingTextWatcher("US"))
        cCPCountry = CountryCodePicker(this)
        cCPCountry.setDefaultCountryUsingNameCode("US")
        cCPCountry.setCountryForPhoneCode(1)
//        cCPCountry.setAutoDetectedCountry(true)
        cCPCountry.registerCarrierNumberEditText(et_new_mobile)

        img_back.setOnClickListener {
            finish()
        }

        btn_continue.setOnClickListener {

            if (et_new_mobile.text.toString() == "") {
                et_new_mobile.error = getString(R.string.please_enter_mobile_number)
            } else if (!cCPCountry.isValidFullNumber) {
                et_new_mobile.error = getString(R.string.enter_valid_mobile)
            } else {

                val oldPhone = SharedPrefrencesUtils.getUserNumber().toString()
                val newPhone = et_new_mobile.text.toString()

                userProfileViewModel.getChangeMobile(token, oldPhone, newPhone)

                // getMobileChange(token, oldPhone, newPhone)
            }
        }
    }

    // init Observer to observe API call initiate and result.
    private fun initChangeMobileObserver() {
        userProfileViewModel.isLoading.observe(this,
            Observer {
                if (it) {
                    ProgressUtils.showLoadingDialog(this)
                } else {
                    ProgressUtils.cancelLoading()
                }
            })

        userProfileViewModel.resultChangeMobileData.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.message?.let { it1 -> makeToast(it1) }
                    val oldPhone = SharedPrefrencesUtils.getUserNumber().toString()
                    val newPhone = et_new_mobile.text.toString()
                    startActivity(
                        Intent(
                            this@ChangeMobileNumberActivity,
                            OTPVerificationActivity::class.java
                        )
                            .putExtra(Constant.ExpireIn, it.data?.data?.expireIn)
                            .putExtra(Constant.RequestId, it.data?.data?.requestId)
                            .putExtra(Constant.ResendType, it.data?.data?.resendType)
                            .putExtra(Constant.isMobileChanged, true)
                            .putExtra(Constant.newMobile, newPhone)
                            .putExtra(Constant.oldMobile, oldPhone)
                    )
                    finish()

                }
                Status.FAILURE -> {

                    makeToast("" + it.errorMsg)
                }
            }
        })
    }


}