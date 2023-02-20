package com.pollvote.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.pollvote.annotations.Status
import com.pollvote.model.login.CheckVoterModel
import com.pollvote.model.login.LoginModel
import com.pollvote.network.ApiClient
import com.pollvote.network.WebResponse
import com.pollvote.network.exceptions.ErrorHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*
* LoginViewModel have two API implementations
* @link callLogin(mobileNo: String?, password: String?, deviceId: String?,deviceType: String?,fcmToken: String?)
* @link callVoterApi(device_id: String, phone_number: String)
*/

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var isViewEnable: MutableLiveData<Boolean> = MutableLiveData()
    var resultData: MutableLiveData<WebResponse<LoginModel>> = MutableLiveData()
    var resultVoterData: MutableLiveData<WebResponse<CheckVoterModel>> = MutableLiveData()

    //    call login API and get result in main thread
    fun callLogin(
        mobileNo: String?,
        password: String?,
        deviceId: String?,
        deviceType: String?,
        fcmToken: String?
    ) {
        val loginObject = JsonObject()

        loginObject.addProperty("MobileNo", mobileNo)
        loginObject.addProperty("Password", password)
        loginObject.addProperty("DeviceId", deviceId)
        loginObject.addProperty("DeviceType", deviceType)
        loginObject.addProperty("FCMToken", fcmToken)
        loginObject.addProperty("RoleId", 404)
        isLoading.postValue(true)
        isViewEnable.postValue(false)

        ApiClient().getClient().getLogin(loginObject).enqueue(object : Callback<LoginModel> {
            override fun onResponse(
                call: Call<LoginModel>,
                response: Response<LoginModel>
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

            override fun onFailure(call: Call<LoginModel?>, error: Throwable) {
                isLoading.postValue(false)
                isViewEnable.postValue(true)
                val errorMsg: String? = ErrorHandler.reportError(error)
                resultData.value = WebResponse(Status.FAILURE, null, errorMsg)
            }
        })
    }


    //    call check voter API and get result in main thread to redirect
    fun callVoterApi(device_id: String, phone_number: String) {
        val requestJson = JsonObject()

        requestJson.addProperty("DeviceId", device_id)
        requestJson.addProperty("MobileNo", phone_number)


        isLoading.postValue(true)
        isViewEnable.postValue(false)

        ApiClient().getClient().checkVoter(requestJson).enqueue(object : Callback<CheckVoterModel> {
            override fun onResponse(
                call: Call<CheckVoterModel>,
                response: Response<CheckVoterModel>
            ) {

                isLoading.postValue(false)
                isViewEnable.postValue(true)
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()
                    result?.status?.let {
                        if (it) {
                            resultVoterData.value = WebResponse(Status.SUCCESS, result, null)
                        } else {
                            resultVoterData.value =
                                WebResponse(Status.FAILURE, null, result.message)
                        }
                    }
                } else {
                    val errorMsg: String = ErrorHandler.reportError(response.code())
                    resultVoterData.value = WebResponse(Status.FAILURE, null, errorMsg)
                }
            }

            override fun onFailure(call: Call<CheckVoterModel?>, error: Throwable) {
                isLoading.postValue(false)
                isViewEnable.postValue(true)
                val errorMsg: String? = ErrorHandler.reportError(error)
                resultVoterData.value = WebResponse(Status.FAILURE, null, errorMsg)
            }
        })
    }
}