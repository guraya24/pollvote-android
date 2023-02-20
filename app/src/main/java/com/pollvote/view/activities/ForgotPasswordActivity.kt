package com.pollvote.view.activities

import android.app.Activity
import android.app.Dialog
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.telephony.PhoneNumberUtils
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsApi
import com.google.android.gms.auth.api.credentials.HintRequest
import com.hbb20.CountryCodePicker
import com.pollvote.R
import com.pollvote.annotations.Status
import com.pollvote.network.Constant
import com.pollvote.utils.ProgressUtils
import com.pollvote.utils.sharedPreference.SharedPrefrencesUtils
import com.pollvote.viewModel.SignUpViewModel
import kotlinx.android.synthetic.main.activity_forgot_password.btn_login
import kotlinx.android.synthetic.main.activity_forgot_password.et_user_id
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*

class ForgotPasswordActivity : BaseActivity() {
    private lateinit var viewModel: SignUpViewModel
    var mobileNo = ""
    var deviceId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_forgot_password)
        // initialize view model
        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        initUI()
        // requestHint()

        // initialize observer
        initObserver()
    }

    private fun initUI() {
        deviceId = SharedPrefrencesUtils.getDeviceId().toString()

        btn_login.setOnClickListener {
            if (et_user_id.text.toString() == "") {

            } else {
                mobileNo = et_user_id.text.toString()

                // call forgot password API using view model
                viewModel.getForgotPassword(
                    mobileNo,
                    deviceId

                )


            }
        }

        /*Mobile Number Format function*/
        et_user_id.addTextChangedListener(PhoneNumberFormattingTextWatcher("US"))
        val cCPCountry = CountryCodePicker(this)
        cCPCountry.setAutoDetectedCountry(true)
        cCPCountry.registerCarrierNumberEditText(et_user_id)
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

        viewModel.resultForgotPasswordData.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {


                    sentPasswordToMailDialog(it.data?.message!!)

                    //it.data?.message?.let { it1 -> makeToast(it1) }


                }

                Status.FAILURE -> {
                    it.errorMsg?.let { it1 -> makeToast(it1) }
                }
            }
        })
    }

    // Construct a request for phone numbers and show the picker
    private fun requestHint() {
        val hintRequest: HintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()
        val intent: PendingIntent = Credentials.getClient(this).getHintPickerIntent(
            hintRequest
        )

        startIntentSenderForResult(
            intent.getIntentSender(),
            100, null, 0, 0, 0
        )
    }

    // Obtain the phone number from the result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                val credential: Credential? = data?.getParcelableExtra(Credential.EXTRA_KEY)
                credential?.id?.let {

                    val formattedNumber: String =
                        PhoneNumberUtils.formatNumber(
                            it,
                            Locale.getDefault().country
                        )
                    et_user_id.setText(formattedNumber)

                }

                // credential.getId();  <-- will need to process phone number string
            } else if (resultCode == CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE) {
                // *** No phone numbers available ***
                Toast.makeText(this, "No phone numbers found", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun sentPasswordToMailDialog(msg:String){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_vote_poll)
        val txt_msg = dialog.findViewById(R.id.txt_msg) as TextView

        val btn_OK = dialog.findViewById(R.id.btn_OK) as Button

        txt_msg.text = msg

        btn_OK.setOnClickListener {
            dialog.dismiss()

            startActivity(
                Intent(
                    this@ForgotPasswordActivity,
                    LoginActivity::class.java
                )

            )
            finish()
        }

        dialog.show()
    }

}