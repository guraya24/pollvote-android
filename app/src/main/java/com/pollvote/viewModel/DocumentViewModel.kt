package com.pollvote.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.pollvote.annotations.Status
import com.pollvote.model.changeMobileNumber.ChangeMobileNoModel
import com.pollvote.model.county.CountyModel
import com.pollvote.model.getDocumentList.DocumentDeleteModel
import com.pollvote.model.getDocumentList.DocumentModel
import com.pollvote.model.getDocumentList.DocumentUploadedModel
import com.pollvote.model.login.LoginModel
import com.pollvote.model.otpVerification.OTPVerificationModel
import com.pollvote.model.register.RegisterModel
import com.pollvote.model.saveAddress.SaveAddressModel
import com.pollvote.model.states.StatesModel
import com.pollvote.network.ApiClient
import com.pollvote.network.WebResponse
import com.pollvote.network.exceptions.ErrorHandler
import com.pollvote.utils.UploadRequestBody
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

/*
* DocumentViewModel have two API implementations
* @link getDocumentList(token: String)
* @link uploadDocumentList(token: String, id: Int, part: MultipartBody.Part, resultUri: String)
*/

class DocumentViewModel(application: Application) : AndroidViewModel(application) {

    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var isViewEnable: MutableLiveData<Boolean> = MutableLiveData()

    var resultDocumentsData: MutableLiveData<WebResponse<DocumentModel>> = MutableLiveData()
    var resultUploadedData: MutableLiveData<WebResponse<DocumentUploadedModel>> = MutableLiveData()
    var resultDeletedDocumentData: MutableLiveData<WebResponse<DocumentDeleteModel>> = MutableLiveData()

    //  call get documents API and get result in main thread
    fun getDocumentList(token: String) {
        isLoading.postValue(true)
        isViewEnable.postValue(false)

        ApiClient().getClient().getDocumentList(token).enqueue(object : Callback<DocumentModel> {
            override fun onResponse(
                call: Call<DocumentModel>,
                response: Response<DocumentModel>
            ) {

                isLoading.postValue(false)
                isViewEnable.postValue(true)
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()
                    result?.status?.let {
                        if (it) {
                            resultDocumentsData.value = WebResponse(Status.SUCCESS, result, null)
                        } else {
                            resultDocumentsData.value =
                                WebResponse(Status.FAILURE, null, result.message)
                        }
                    }
                } else {
                    val errorMsg: String = ErrorHandler.reportError(response.code())
                    resultDocumentsData.value = WebResponse(Status.FAILURE, null, errorMsg)
                }
            }

            override fun onFailure(call: Call<DocumentModel?>, error: Throwable) {
                isLoading.postValue(false)
                isViewEnable.postValue(true)
                val errorMsg: String? = ErrorHandler.reportError(error)
                resultDocumentsData.value = WebResponse(Status.FAILURE, null, errorMsg)
            }
        })
    }

    // upload pending documents using multipart data
    fun uploadDocumentList(token: String, id: Int, path: String)  {
        isLoading.postValue(true)
        isViewEnable.postValue(false)

        val file = File(path)
        val body = UploadRequestBody(file, "image/*")
        val part = MultipartBody.Part.createFormData("file", file.name, body)

        ApiClient().getClient().uploadDocument(token, id, part)
            .enqueue(object : Callback<DocumentUploadedModel> {
                override fun onResponse(
                    call: Call<DocumentUploadedModel>,
                    response: Response<DocumentUploadedModel>
                ) {

                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    if (response.isSuccessful && response.body() != null) {
                        val result = response.body()
                        result?.status?.let {
                            if (it) {
                                resultUploadedData.value = WebResponse(Status.SUCCESS, result, null)
                            } else {
                                resultUploadedData.value =
                                    WebResponse(Status.FAILURE, null, result.message)
                            }
                        }
                    } else {
                        val errorMsg: String = ErrorHandler.reportError(response.code())
                        resultUploadedData.value = WebResponse(Status.FAILURE, null, errorMsg)
                    }
                }

                override fun onFailure(call: Call<DocumentUploadedModel?>, error: Throwable) {
                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    val errorMsg: String? = ErrorHandler.reportError(error)
                    resultUploadedData.value = WebResponse(Status.FAILURE, null, errorMsg)
                }
            })
    }

    // upload pending documents using multipart data
    fun callDeleteDocument(token: String, id: Int)  {
        isLoading.postValue(true)
        isViewEnable.postValue(false)


        ApiClient().getClient().deleteDocument(token, id)
            .enqueue(object : Callback<DocumentDeleteModel> {
                override fun onResponse(
                    call: Call<DocumentDeleteModel>,
                    response: Response<DocumentDeleteModel>
                ) {

                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    if (response.isSuccessful && response.body() != null) {
                        val result = response.body()
                        result?.status?.let {
                            if (it) {
                                resultDeletedDocumentData.value = WebResponse(Status.SUCCESS, result, null)
                            } else {
                                resultDeletedDocumentData.value =
                                    WebResponse(Status.FAILURE, null, result.message)
                            }
                        }
                    } else {
                        val errorMsg: String = ErrorHandler.reportError(response.code())
                        resultDeletedDocumentData.value = WebResponse(Status.FAILURE, null, errorMsg)
                    }
                }

                override fun onFailure(call: Call<DocumentDeleteModel?>, error: Throwable) {
                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    val errorMsg: String? = ErrorHandler.reportError(error)
                    resultDeletedDocumentData.value = WebResponse(Status.FAILURE, null, errorMsg)
                }
            })
    }

}