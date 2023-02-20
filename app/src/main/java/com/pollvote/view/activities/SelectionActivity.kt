package com.pollvote.view.activities


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hbb20.CountryCodePicker
import com.pollvote.R
import com.pollvote.annotations.Status
import com.pollvote.network.Constant
import com.pollvote.utils.ProgressUtils
import com.pollvote.utils.Static
import com.pollvote.utils.sharedPreference.SharedPrefrencesUtils
import com.pollvote.viewModel.LoginViewModel
import kotlinx.android.synthetic.main.selection_activity.*


@Suppress("DEPRECATION")
class SelectionActivity : BaseActivity() {

    private val WAIT_TIME = 3 * 1000L
    private var startedAt: Long = 0
    private var isLogin: Boolean = false
    private lateinit var viewModel: LoginViewModel
    private var mobileNumber: String = ""

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.selection_activity)
        // initialize view model
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        // initialize observer
        initObserver()

        val device_id = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )
        SharedPrefrencesUtils.setDeviceId(device_id)
        val setToolbarTitle = Static()
        setToolbarTitle.statusBarColor(this)
        isLogin = SharedPrefrencesUtils.isUserLogin()!!
        startedAt = System.currentTimeMillis()

        if (isLogin) {
            proceedToNext()
        }
        btn_login.setOnClickListener {
            proceedLogin()
        }

        btn_sign.setOnClickListener {
            proceedSignUp()
        }
    }

    private fun proceedToNext() {
        ProgressUtils.showLoadingDialog(this)
        val elapsedTime = System.currentTimeMillis() - startedAt
        val remainingTime = WAIT_TIME - elapsedTime
        if (remainingTime > 0) {
            Handler().postDelayed({
                ProgressUtils.cancelLoading()
                proceedMain()
                //afterSplash()
            }, remainingTime)
        }
    }

    // open login screen
    private fun proceedLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    // open signup screen
    private fun proceedSignUp() {
        openDialogPhoneNumberInput()
    }

    // open main home screen
    private fun proceedMain() {
        startActivity(Intent(this, EventListActivity::class.java))
        finishAffinity()
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

        viewModel.resultVoterData.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    // verified with device id matched and without matched
                    if (it.data?.code == 1000) {
                        val intent = Intent(this@SelectionActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        intent.putExtra(Constant.mobile, mobileNumber)
                        startActivity(intent)
                        makeToast(it.data?.message!!)
                    }
                    // user exists but not verified
                    if (it.data?.code == 1001) {
                        val intent = Intent(this@SelectionActivity, SignUpActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        intent.putExtra(Constant.mobile, mobileNumber)
                        intent.putExtra("data", it.data?.data)

                        startActivity(intent)
                        makeToast(it.data?.message!!)
                    }

                    // not exists
                    if (it.data?.code == 1002) {
                        val intent = Intent(this@SelectionActivity, SignUpActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        intent.putExtra(Constant.mobile, mobileNumber)
                        startActivity(intent)
                        makeToast(it.data?.message!!)
                    }

                    // device mismatch
                    if (it.data?.code == 1003) {
                        val intent = Intent(this@SelectionActivity, SignUpActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        intent.putExtra(Constant.mobile, mobileNumber)
                        intent.putExtra("data", it.data?.data)
                        startActivity(intent)
                        makeToast(it.data?.message!!)
                    }

                }
                Status.FAILURE -> {
                    it.errorMsg?.let { it1 -> makeToast(it1) }
                    makeToast("" + it.errorMsg)
                }
            }
        })
    }

    // dialog to fetch phone number from user
    private fun openDialogPhoneNumberInput() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_phone_number)
        val etPhoneNumber = dialog.findViewById(R.id.et_phone_number) as EditText

        val imgClose = dialog.findViewById(R.id.img_close) as ImageView

        etPhoneNumber.addTextChangedListener(PhoneNumberFormattingTextWatcher("US"))
//        etPhoneNumber.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                if (!del) {
//                    if (s?.length == 3) {
//                        phone = "($s) "
//                        etPhoneNumber.setText(phone)
//                        etPhoneNumber.setSelection(phone.length)
//                    }
//
//                    if (s?.length == 9) {
//                        phone = "$s-"
//                        etPhoneNumber.setText(phone)
//                        etPhoneNumber.setSelection(phone.length)
//                    }
//                }
//            }
//
//        })
//
//        etPhoneNumber.setOnKeyListener(View.OnKeyListener { view, keyCode, event ->
//            if (keyCode == KeyEvent.KEYCODE_DEL && phone.startsWith("(")) {
//                del = true
//                phone = etPhoneNumber.text.toString()
//                phone = phone.replace("(", "", true)
//                phone = phone.replace(")", "")
//                phone = phone.replace("-", "")
//                phone = phone.replace(" ", "")
//                etPhoneNumber.setText(phone)
//                etPhoneNumber.setSelection(phone.length)
//
//                return@OnKeyListener true
//            } else if (keyCode == KeyEvent.KEYCODE_DEL) {
//                del = true
//            }else{
//                del=false
//            }
//            false
//        })

        val cCPCountry = CountryCodePicker(this)
//        cCPCountry.setAutoDetectedCountry(true)
        cCPCountry.setDefaultCountryUsingNameCode("US")
        cCPCountry.setCountryForPhoneCode(1)
        cCPCountry.registerCarrierNumberEditText(etPhoneNumber)

        val btnAddNumber = dialog.findViewById(R.id.btn_add_number) as Button

        btnAddNumber.setOnClickListener {

            if (etPhoneNumber.text.isEmpty()) {
                etPhoneNumber.error = getString(R.string.enter_mobile)
            } else if (!cCPCountry.isValidFullNumber) {//(!isValidPhone(etPhoneNumber.text.toString())) {
                etPhoneNumber.error = getString(R.string.enter_valid_mobile)
            } else {
                mobileNumber = etPhoneNumber.text.toString()
                dialog.dismiss()

                // call voter check API to check user sattus
                viewModel.callVoterApi(
                    SharedPrefrencesUtils.getDeviceId()!!, etPhoneNumber.text.toString()
                )
            }

        }

        imgClose.setOnClickListener {
            dialog.cancel()
        }

        dialog.show()
    }
}