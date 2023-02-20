package com.pollvote.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.pollvote.annotations.Status
import com.pollvote.model.changeMobileNumber.ChangeMobileNoModel
import com.pollvote.model.changePassword.ChangePasswordModel
import com.pollvote.model.county.CountyModel
import com.pollvote.model.getDocumentList.DocumentUploadedModel
import com.pollvote.model.profileDetail.ProfileImageModel
import com.pollvote.model.profileDetail.ProfileModel
import com.pollvote.model.saveAddress.SaveAddressModel
import com.pollvote.model.states.StatesModel
import com.pollvote.network.ApiClient
import com.pollvote.network.WebResponse
import com.pollvote.network.exceptions.ErrorHandler
import com.pollvote.utils.UploadRequestBody
import kotlinx.android.synthetic.main.activity_change_password.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

/*
* SignUpViewModel have three API implementations
* @link callSignUp( firstName: String, drivingLicence: String, mobileNo: String, email: String,
                   password: String, deviceId: String, deviceType: String, fcmToken: String, voterId: String)
* @link callVoterApi(device_id: String, phone_number: String)
* @link getMobileVerify(mobileNo: String, newMobileNo: String, otp: String, token: String)
*/
class UserProfileViewModel(application: Application) : AndroidViewModel(application) {

    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var isViewEnable: MutableLiveData<Boolean> = MutableLiveData()


    //    call get Change password API and get result in main thread
    var resultChangePasswordData: MutableLiveData<WebResponse<ChangePasswordModel>> =
        MutableLiveData()
    fun getChangePassword(token: String, oldPassword: String, newPassword: String) {
        isLoading.postValue(true)
        isViewEnable.postValue(false)

        val passwordObject = JsonObject()

        passwordObject.addProperty("OldPassword", oldPassword)
        passwordObject.addProperty("NewPassword", newPassword)

        ApiClient().getClient().getChangePassword(token, passwordObject)
            .enqueue(object : Callback<ChangePasswordModel> {
                override fun onResponse(
                    call: Call<ChangePasswordModel>,
                    response: Response<ChangePasswordModel>
                ) {

                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    if (response.isSuccessful && response.body() != null) {
                        val result = response.body()
                        result?.status?.let {
                            if (it) {
                                resultChangePasswordData.value =
                                    WebResponse(Status.SUCCESS, result, null)
                            } else {
                                resultChangePasswordData.value =
                                    WebResponse(Status.FAILURE, null, result.message)
                            }
                        }
                    } else {
                        val errorMsg: String = ErrorHandler.reportError(response.code())
                        resultChangePasswordData.value = WebResponse(Status.FAILURE, null, errorMsg)
                    }
                }

                override fun onFailure(call: Call<ChangePasswordModel?>, error: Throwable) {
                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    val errorMsg: String? = ErrorHandler.reportError(error)
                    resultChangePasswordData.value = WebResponse(Status.FAILURE, null, errorMsg)
                }
            })
    }

    //    call get Change Mobile Number API and get result in main thread
    var resultChangeMobileData: MutableLiveData<WebResponse<ChangeMobileNoModel>> =
        MutableLiveData()
    fun getChangeMobile(token: String, oldMobile: String, newMobile: String) {
        isLoading.postValue(true)
        isViewEnable.postValue(false)

        val mobileObject = JsonObject()
        mobileObject.addProperty("OldPhoneNumber", oldMobile)
        mobileObject.addProperty("NewPhoneNumber", newMobile)

        ApiClient().getClient().getChangeMobileNumber(token, mobileObject)
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
                                resultChangeMobileData.value =
                                    WebResponse(Status.SUCCESS, result, null)
                            } else {
                                resultChangeMobileData.value =
                                    WebResponse(Status.FAILURE, null, result.message)
                            }
                        }
                    } else {
                        val errorMsg: String = ErrorHandler.reportError(response.code())
                        resultChangeMobileData.value = WebResponse(Status.FAILURE, null, errorMsg)
                    }
                }

                override fun onFailure(call: Call<ChangeMobileNoModel?>, error: Throwable) {
                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    val errorMsg: String? = ErrorHandler.reportError(error)
                    resultChangeMobileData.value = WebResponse(Status.FAILURE, null, errorMsg)
                }
            })
    }


    //    call get  personal detail API and get result in main thread
    var resultProfileDetailData: MutableLiveData<WebResponse<ProfileModel>> =
        MutableLiveData()

    fun getProfileDetail(
        token: String) {
        isLoading.postValue(true)
        isViewEnable.postValue(false)

        ApiClient().getClient().getProfileDetail(token)
            .enqueue(object : Callback<ProfileModel> {
                override fun onResponse(
                    call: Call<ProfileModel>,
                    response: Response<ProfileModel>
                ) {

                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    if (response.isSuccessful && response.body() != null) {
                        val result = response.body()
                        result?.status?.let {
                            if (it) {
                                resultProfileDetailData.value = WebResponse(Status.SUCCESS, result, null)
                            } else {
                                resultProfileDetailData.value = WebResponse(Status.FAILURE, null, result.message)
                            }
                        }
                    } else {
                        val errorMsg: String = ErrorHandler.reportError(response.code())
                        resultProfileDetailData.value =
                            WebResponse(Status.FAILURE, null, errorMsg)
                    }
                }

                override fun onFailure(call: Call<ProfileModel?>, error: Throwable) {
                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    val errorMsg: String? = ErrorHandler.reportError(error)
                    resultProfileDetailData.value =
                        WebResponse(Status.FAILURE, null, errorMsg)
                }
            })
    }

    //    call get update personal detail API and get result in main thread
    var resultUpdatePesonalDetailData: MutableLiveData<WebResponse<SaveAddressModel>> = MutableLiveData()
    fun callUpdatePersonalDetail(
        token: String,
        userName: String,
        address: String,
        town: String,
        provinceId: Int,
        countyId: Int,
        zip: String,
        dob: String,
        gender: String
    ) {
        isLoading.postValue(true)
        isViewEnable.postValue(false)

        val detailObject = JsonObject()
        detailObject.addProperty("username", userName)
        detailObject.addProperty("AddressLine1", address)
        detailObject.addProperty("TwonShip", town)
        detailObject.addProperty("City", "")
        detailObject.addProperty("CountyId", countyId)
        detailObject.addProperty("StateId", provinceId)
        detailObject.addProperty("Gender", gender)
        detailObject.addProperty("DOB", dob)
        detailObject.addProperty("ZipCode", zip)
        detailObject.addProperty("CountryId", 1)


        ApiClient().getClient().postSaveAddress(token, detailObject)
            .enqueue(object : Callback<SaveAddressModel> {
                override fun onResponse(
                    call: Call<SaveAddressModel>,
                    response: Response<SaveAddressModel>
                ) {

                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    if (response.isSuccessful && response.body() != null) {
                        val result = response.body()
                        result?.status?.let {
                            if (it) {
                                resultUpdatePesonalDetailData.value = WebResponse(Status.SUCCESS, result, null)
                            } else {
                                resultUpdatePesonalDetailData.value = WebResponse(Status.FAILURE, null, result.message)
                            }
                        }
                    } else {
                        val errorMsg: String = ErrorHandler.reportError(response.code())
                        resultUpdatePesonalDetailData.value =
                            WebResponse(Status.FAILURE, null, errorMsg)
                    }
                }

                override fun onFailure(call: Call<SaveAddressModel?>, error: Throwable) {
                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    val errorMsg: String? = ErrorHandler.reportError(error)
                    resultUpdatePesonalDetailData.value =
                        WebResponse(Status.FAILURE, null, errorMsg)
                }
            })
    }




    //    call upload profile image API and get result in main thread
    var resultProfileImageData: MutableLiveData<WebResponse<ProfileImageModel>> =
        MutableLiveData()

    // upload profile image using multipart data
    fun uploadProfileImage(token: String, path: String)  {
        isLoading.postValue(true)
        isViewEnable.postValue(false)

        val file = File(path)
        val body = UploadRequestBody(file, "image/*")
        val part = MultipartBody.Part.createFormData("file", file.name, body)

        ApiClient().getClient().uploadProfileImage(token, part)
            .enqueue(object : Callback<ProfileImageModel> {
                override fun onResponse(
                    call: Call<ProfileImageModel>,
                    response: Response<ProfileImageModel>
                ) {

                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    if (response.isSuccessful && response.body() != null) {
                        val result = response.body()
                        result?.status?.let {
                            if (it) {
                                resultProfileImageData.value = WebResponse(Status.SUCCESS, result, null)
                            } else {
                                resultProfileImageData.value =
                                    WebResponse(Status.FAILURE, null, result.message)
                            }
                        }
                    } else {
                        val errorMsg: String = ErrorHandler.reportError(response.code())
                        resultProfileImageData.value = WebResponse(Status.FAILURE, null, errorMsg)
                    }
                }

                override fun onFailure(call: Call<ProfileImageModel?>, error: Throwable) {
                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    val errorMsg: String? = ErrorHandler.reportError(error)
                    resultProfileImageData.value = WebResponse(Status.FAILURE, null, errorMsg)
                }
            })
    }
}