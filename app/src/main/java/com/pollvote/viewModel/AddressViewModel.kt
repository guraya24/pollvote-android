package com.pollvote.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.pollvote.annotations.Status
import com.pollvote.model.changeMobileNumber.ChangeMobileNoModel
import com.pollvote.model.county.CountyModel
import com.pollvote.model.login.LoginModel
import com.pollvote.model.otpVerification.OTPVerificationModel
import com.pollvote.model.register.RegisterModel
import com.pollvote.model.saveAddress.SaveAddressModel
import com.pollvote.model.states.StatesModel
import com.pollvote.network.ApiClient
import com.pollvote.network.WebResponse
import com.pollvote.network.exceptions.ErrorHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*
* AddressViewModel have three API implementations
* @link getStateList(id: String,token: String)
* @link getCountyList(id: String,token: String)
* @link saveAddressDetail(token: String, address: String,
        town: String, provinceId: Int, countyId: Int, zip: String, dob: String, gender: String)
*/

class AddressViewModel(application: Application) : AndroidViewModel(application) {

    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var isViewEnable: MutableLiveData<Boolean> = MutableLiveData()

    var resultStatesData: MutableLiveData<WebResponse<StatesModel>> = MutableLiveData()
    var resultCountyData: MutableLiveData<WebResponse<CountyModel>> = MutableLiveData()
    var resultSaveAddressData: MutableLiveData<WebResponse<SaveAddressModel>> =
        MutableLiveData()

    //    call get states API and get result in main thread
    fun getStateList(id: String, token: String) {
        isLoading.postValue(true)
        isViewEnable.postValue(false)

        ApiClient().getClient().getState(token, id).enqueue(object : Callback<StatesModel> {
            override fun onResponse(
                call: Call<StatesModel>,
                response: Response<StatesModel>
            ) {

                isLoading.postValue(false)
                isViewEnable.postValue(true)
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()
                    result?.status?.let {
                        if (it) {
                            resultStatesData.value = WebResponse(Status.SUCCESS, result, null)
                        } else {
                            resultStatesData.value =
                                WebResponse(Status.FAILURE, null, result.message)
                        }
                    }
                } else {
                    val errorMsg: String = ErrorHandler.reportError(response.code())
                    resultStatesData.value = WebResponse(Status.FAILURE, null, errorMsg)
                }
            }

            override fun onFailure(call: Call<StatesModel?>, error: Throwable) {
                isLoading.postValue(false)
                isViewEnable.postValue(true)
                val errorMsg: String? = ErrorHandler.reportError(error)
                resultStatesData.value = WebResponse(Status.FAILURE, null, errorMsg)
            }
        })
    }

    //call get county API and get result in main thread
    fun getCountyList(id: String, token: String) {
        isLoading.postValue(true)
        isViewEnable.postValue(false)

        ApiClient().getClient().getCountyList(token, id)
            .enqueue(object : Callback<CountyModel> {
                override fun onResponse(
                    call: Call<CountyModel>,
                    response: Response<CountyModel>
                ) {

                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    if (response.isSuccessful && response.body() != null) {
                        val result = response.body()
                        result?.status?.let {
                            if (it) {
                                resultCountyData.value = WebResponse(Status.SUCCESS, result, null)
                            } else {
                                resultCountyData.value =
                                    WebResponse(Status.FAILURE, null, result.message)
                            }
                        }
                    } else {
                        val errorMsg: String = ErrorHandler.reportError(response.code())
                        resultCountyData.value = WebResponse(Status.FAILURE, null, errorMsg)
                    }
                }

                override fun onFailure(call: Call<CountyModel?>, error: Throwable) {
                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    val errorMsg: String? = ErrorHandler.reportError(error)
                    resultCountyData.value = WebResponse(Status.FAILURE, null, errorMsg)
                }
            })
    }

    // call save address verification API
    fun saveAddressDetail(token: String, address: String,
        town: String, provinceId: Int, countyId: Int, zip: String, dob: String, gender: String) {

        val detailObject = JsonObject()
        detailObject.addProperty("AddressLine1", address)
        detailObject.addProperty("TwonShip", town)
        detailObject.addProperty("City", "")
        detailObject.addProperty("CountyId", countyId)
        detailObject.addProperty("StateId", provinceId)
        detailObject.addProperty("Gender", gender)
        detailObject.addProperty("DOB", dob)
        detailObject.addProperty("ZipCode", zip)
        detailObject.addProperty("CountryId", 1)

        isLoading.postValue(true)
        isViewEnable.postValue(false)

        ApiClient().getClient().postSaveAddress(token, detailObject)
            .enqueue(object : Callback<SaveAddressModel> {
                override fun onResponse(
                    call: Call<SaveAddressModel>,
                    response: Response<SaveAddressModel>) {

                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    if (response.isSuccessful && response.body() != null) {
                        val result = response.body()
                        result?.status?.let {
                            if (it) {
                                resultSaveAddressData.value =
                                    WebResponse(Status.SUCCESS, result, null)
                            } else {
                                resultSaveAddressData.value =
                                    WebResponse(Status.FAILURE, null, result.message)
                            }
                        }
                    } else {
                        val errorMsg: String = ErrorHandler.reportError(response.code())
                        resultSaveAddressData.value = WebResponse(Status.FAILURE, null, errorMsg)
                    }
                }

                override fun onFailure(call: Call<SaveAddressModel?>, error: Throwable) {
                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    val errorMsg: String? = ErrorHandler.reportError(error)
                    resultSaveAddressData.value = WebResponse(Status.FAILURE, null, errorMsg)
                }
            })
    }
}