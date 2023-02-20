package com.pollvote.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.pollvote.annotations.Status
import com.pollvote.model.changeMobileNumber.ChangeMobileNoModel
import com.pollvote.model.otpVerification.OTPVerificationModel
import com.pollvote.model.register.RegisterModel
import com.pollvote.network.ApiClient
import com.pollvote.network.WebResponse
import com.pollvote.network.exceptions.ErrorHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*
* SignUpViewModel have three API implementations
* @link callSignUp( firstName: String, drivingLicence: String, mobileNo: String, email: String,
                   password: String, deviceId: String, deviceType: String, fcmToken: String, voterId: String)
* @link callVoterApi(device_id: String, phone_number: String)
* @link getMobileVerify(mobileNo: String, newMobileNo: String, otp: String, token: String)
*/

class SignUpViewModel(application: Application) : AndroidViewModel(application) {

    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var isViewEnable: MutableLiveData<Boolean> = MutableLiveData()

    /*Register*/
    var resultData: MutableLiveData<WebResponse<RegisterModel>> = MutableLiveData()

    /*Resend OTP*/
    var resultResendOTPData: MutableLiveData<WebResponse<RegisterModel>> = MutableLiveData()

    /*OTP Verify*/
    var resultOTPData: MutableLiveData<WebResponse<OTPVerificationModel>> = MutableLiveData()

    /*Change Mobile*/
    var resultChangeMobileNoData: MutableLiveData<WebResponse<ChangeMobileNoModel>> =
        MutableLiveData()

    /*Forgot Password*/
    var resultForgotPasswordData: MutableLiveData<WebResponse<RegisterModel>> = MutableLiveData()

    //call SignUp API and get result in main thread
    fun callSignUp(
        firstName: String, drivingLicence: String, mobileNo: String, email: String,
        password: String, deviceId: String, deviceType: String, fcmToken: String, voterId: String
    ) {
        val paramObject = JsonObject()

        paramObject.addProperty("firstName", firstName)
        paramObject.addProperty("lastName", "")
        paramObject.addProperty("drivingLicence", drivingLicence)
        paramObject.addProperty("voterId", voterId)
        paramObject.addProperty("mobileNo", mobileNo)
        paramObject.addProperty("email", email)
        paramObject.addProperty("Password", password)
        paramObject.addProperty("deviceId", deviceId)
        paramObject.addProperty("deviceType", deviceType)
        paramObject.addProperty("FCMToken", fcmToken)

        isLoading.postValue(true)
        isViewEnable.postValue(false)

        ApiClient().getClient().getRegister(paramObject).enqueue(object : Callback<RegisterModel> {
            override fun onResponse(
                call: Call<RegisterModel>,
                response: Response<RegisterModel>
            ) {

                isLoading.postValue(false)
                isViewEnable.postValue(true)
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()
                    result?.status?.let {
                        if (it) {
                            resultData.value = WebResponse(Status.SUCCESS, result, null)
                        } else {
                            resultData.value = WebResponse(Status.FAILURE, null, result.message)
                        }
                    }
                } else {
                    val errorMsg: String = ErrorHandler.reportError(response.code())
                    resultData.value = WebResponse(Status.FAILURE, null, errorMsg)
                }
            }

            override fun onFailure(call: Call<RegisterModel?>, error: Throwable) {
                isLoading.postValue(false)
                isViewEnable.postValue(true)
                val errorMsg: String? = ErrorHandler.reportError(error)
                resultData.value = WebResponse(Status.FAILURE, null, errorMsg)
            }
        })
    }

    //call otp verify API and get result in main thread
    fun getOTPVerify(mobileNo: String, deviceId: String, otp: String) {
        val paramObject = JsonObject()

        paramObject.addProperty("mobileNo", mobileNo)
        paramObject.addProperty("deviceId", deviceId)
        paramObject.addProperty("Otp", otp)

        isLoading.postValue(true)
        isViewEnable.postValue(false)

        ApiClient().getClient().getOTPVerify(paramObject)
            .enqueue(object : Callback<OTPVerificationModel> {
                override fun onResponse(
                    call: Call<OTPVerificationModel>,
                    response: Response<OTPVerificationModel>
                ) {

                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    if (response.isSuccessful && response.body() != null) {
                        val result = response.body()
                        result?.status?.let {
                            if (it) {
                                resultOTPData.value = WebResponse(Status.SUCCESS, result, null)
                            } else {
                                resultOTPData.value =
                                    WebResponse(Status.FAILURE, null, result.message)
                            }
                        }
                    } else {
                        val errorMsg: String = ErrorHandler.reportError(response.code())
                        resultOTPData.value = WebResponse(Status.FAILURE, null, errorMsg)
                    }
                }

                override fun onFailure(call: Call<OTPVerificationModel?>, error: Throwable) {
                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    val errorMsg: String? = ErrorHandler.reportError(error)
                    resultOTPData.value = WebResponse(Status.FAILURE, null, errorMsg)
                }
            })
    }


    //call resend otp  API and get result in main thread
    fun getResendOTPVerify(mobileNo: String, deviceId: String, resendType: String, requestId: Int) {
        val paramObject = JsonObject()

        paramObject.addProperty("mobileNo", mobileNo)
        paramObject.addProperty("deviceId", deviceId)
        paramObject.addProperty("resendType", resendType)
        paramObject.addProperty("requestId", requestId)

        isLoading.postValue(true)
        isViewEnable.postValue(false)

        ApiClient().getClient().getResendOTP(paramObject)
            .enqueue(object : Callback<RegisterModel> {
                override fun onResponse(
                    call: Call<RegisterModel>,
                    response: Response<RegisterModel>
                ) {

                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    if (response.isSuccessful && response.body() != null) {
                        val result = response.body()
                        result?.status?.let {
                            if (it) {
                                resultResendOTPData.value =
                                    WebResponse(Status.SUCCESS, result, null)
                            } else {
                                resultResendOTPData.value =
                                    WebResponse(Status.FAILURE, null, result.message)
                            }
                        }
                    } else {
                        val errorMsg: String = ErrorHandler.reportError(response.code())
                        resultResendOTPData.value = WebResponse(Status.FAILURE, null, errorMsg)
                    }
                }

                override fun onFailure(call: Call<RegisterModel?>, error: Throwable) {
                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    val errorMsg: String? = ErrorHandler.reportError(error)
                    resultResendOTPData.value = WebResponse(Status.FAILURE, null, errorMsg)
                }
            })
    }


    //call Forgot password  API and get result in main thread
    fun getForgotPassword(mobileNo: String, deviceId: String) {
        val paramObject = JsonObject()

        paramObject.addProperty("phoneNumber", mobileNo)
        paramObject.addProperty("deviceId", deviceId)
        paramObject.addProperty("deviceType", "")


        isLoading.postValue(true)
        isViewEnable.postValue(false)

        ApiClient().getClient().getForgotPassword(paramObject)
            .enqueue(object : Callback<RegisterModel> {
                override fun onResponse(
                    call: Call<RegisterModel>,
                    response: Response<RegisterModel>
                ) {

                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    if (response.isSuccessful && response.body() != null) {
                        val result = response.body()
                        result?.status?.let {
                            if (it) {
                                resultForgotPasswordData.value =
                                    WebResponse(Status.SUCCESS, result, null)
                            } else {
                                resultForgotPasswordData.value =
                                    WebResponse(Status.FAILURE, null, result.message)
                            }
                        }
                    } else {
                        val errorMsg: String = ErrorHandler.reportError(response.code())
                        resultForgotPasswordData.value = WebResponse(Status.FAILURE, null, errorMsg)
                    }
                }

                override fun onFailure(call: Call<RegisterModel?>, error: Throwable) {
                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    val errorMsg: String? = ErrorHandler.reportError(error)
                    resultForgotPasswordData.value = WebResponse(Status.FAILURE, null, errorMsg)
                }
            })
    }

    // call change mobile number verification API
    fun getMobileVerify(mobileNo: String, newMobileNo: String, otp: String, token: String) {
        val paramObject = JsonObject()

        paramObject.addProperty("OldPhoneNumber", mobileNo)
        paramObject.addProperty("NewPhoneNumber", newMobileNo)
        paramObject.addProperty("Otp", otp)


        isLoading.postValue(true)
        isViewEnable.postValue(false)

        ApiClient().getClient().getChangeMobileNumberVerify(token, paramObject)
            .enqueue(object : Callback<ChangeMobileNoModel> {
                override fun onResponse(
                    call: Call<ChangeMobileNoModel>,
                    response: Response<ChangeMobileNoModel>
                ) {

                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    if (response.isSuccessful && response.body() != null) {
                        val result = response.body()
                        result?.status?.let {
                            if (it) {
                                resultChangeMobileNoData.value =
                                    WebResponse(Status.SUCCESS, result, null)
                            } else {
                                resultChangeMobileNoData.value =
                                    WebResponse(Status.FAILURE, null, result.message)
                            }
                        }
                    } else {
                        val errorMsg: String = ErrorHandler.reportError(response.code())
                        resultChangeMobileNoData.value = WebResponse(Status.FAILURE, null, errorMsg)
                    }
                }

                override fun onFailure(call: Call<ChangeMobileNoModel?>, error: Throwable) {
                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    val errorMsg: String? = ErrorHandler.reportError(error)
                    resultChangeMobileNoData.value = WebResponse(Status.FAILURE, null, errorMsg)
                }
            })
    }
}