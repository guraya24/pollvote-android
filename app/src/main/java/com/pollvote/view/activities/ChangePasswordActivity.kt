package com.pollvote.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pollvote.R
import com.pollvote.annotations.Status
import com.pollvote.utils.ProgressUtils
import com.pollvote.utils.sharedPreference.SharedPrefrencesUtils
import com.pollvote.utils.Static
import com.pollvote.viewModel.UserProfileViewModel
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.activity_change_password.btn_continue
import kotlinx.android.synthetic.main.activity_change_password.img_back

import java.util.regex.Pattern

class ChangePasswordActivity : BaseActivity() {
    var token = ""
    private lateinit var userProfileViewModel: UserProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        val setToolbarTitle = Static()
        setToolbarTitle.statusBarColor(this)
        userProfileViewModel = ViewModelProvider(this).get(UserProfileViewModel::class.java)

        initUI()
        initChangePasswordObserver()
    }

    private fun initUI() {

        img_back.setOnClickListener {
            finish()
        }
        btn_continue.setOnClickListener {
            if (et_old_password.text.toString() == "") {
                et_old_password.error = getString(R.string.please_enter_old_password)
            } /*else if (!isValidPassword(et_old_password)) {
                Log.e("sd", "sd")
            }*/ else if (et_new_password.text.toString() == "") {
                et_new_password.error = getString(R.string.please_enter_new_password)
            } else if (et_new_password.text.toString().length<8) {
                et_new_password.error = getString(R.string.enter_password_length)
                Log.e("sd", "sd")
            } else if (et_confirm_password.text.toString() == "") {
                et_confirm_password.error = getString(R.string.please_enter_confirm_password)
            } else if (et_confirm_password.text.toString() != et_new_password.text.toString()) {

                makeToast(getString(R.string.new_confirm_password_not_matched))

                et_confirm_password.error = getString(R.string.new_confirm_password_not_matched)
            } else {
                token = SharedPrefrencesUtils.getToken().toString()
                userProfileViewModel.getChangePassword(
                    token,
                    et_old_password.text.toString(),
                    et_new_password.text.toString()
                )

            }

        }

    }


//    // validation for password
//    private fun isValidPassword(et_password: CustomETVPoppinsRegular): Boolean {
//        val str = et_password.getText()
//        var valid = true
//
//        // Password policy check
//        // Password should be minimum minimum 8 characters long
//        if (str?.length!! < 8) {
//            valid = false
//        }
//        // Password should contain at least one number
//        var exp = ".*[0-9].*"
//        var pattern = Pattern.compile(exp, Pattern.CASE_INSENSITIVE)
//        var matcher = pattern.matcher(str)
//        if (!matcher.matches()) {
//            valid = false
//        }
//
//        // Password should contain at least one capital letter
//        exp = ".*[A-Z].*"
//        pattern = Pattern.compile(exp)
//        matcher = pattern.matcher(str)
//        if (!matcher.matches()) {
//            valid = false
//        }
//
//        // Password should contain at least one small letter
//        exp = ".*[a-z].*"
//        pattern = Pattern.compile(exp)
//        matcher = pattern.matcher(str)
//        if (!matcher.matches()) {
//            valid = false
//        }
//
//        // Password should contain at least one special character
//        // Allowed special characters : "~!@#$%^&*()-_=+|/,."';:{}[]<>?"
//        exp = ".*[~!@#\$%\\^&*()\\-_=+\\|\\[{\\]};:'\",<.>/?].*"
//        pattern = Pattern.compile(exp)
//        matcher = pattern.matcher(str)
//        if (!matcher.matches()) {
//            valid = false
//        }
//
//        // Set error if required
//
//        val error: String? =
//            if (valid) null else getString(R.string.password_policy)
//        et_password.error = error
//
//
//        return valid
//    }
//

    // init Observer to observe API call initiate and result.
    private fun initChangePasswordObserver() {
        userProfileViewModel.isLoading.observe(this,
            Observer {
                if (it) {
                    ProgressUtils.showLoadingDialog(this)
                } else {
                    ProgressUtils.cancelLoading()
                }
            })

        userProfileViewModel.resultChangePasswordData.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    startActivity(
                        Intent(
                            this@ChangePasswordActivity,
                            ProfileDetail::class.java
                        )
                    )
                    finish()
                    it.data?.message?.let { it1 -> makeToast(it1) }


                }
                Status.FAILURE -> {
                    makeToast("" + it.errorMsg)
                }
            }
        })
    }


}