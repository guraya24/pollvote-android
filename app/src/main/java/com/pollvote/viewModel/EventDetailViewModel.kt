package com.pollvote.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonArray
import com.pollvote.annotations.Status
import com.pollvote.model.event.EventModel
import com.pollvote.model.eventDetail.EventDetailModel
import com.pollvote.model.pollCandidate.PollCandidateModel
import com.pollvote.model.pollVote.PollVoteModel
import com.pollvote.model.velidateVoter.ValidateVoterModel
import com.pollvote.network.ApiClient
import com.pollvote.network.WebResponse
import com.pollvote.network.exceptions.ErrorHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventDetailViewModel(application: Application) : AndroidViewModel(application) {

    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var isViewEnable: MutableLiveData<Boolean> = MutableLiveData()

    //    call Event Detail API and get result in main thread
    var resultData: MutableLiveData<WebResponse<EventDetailModel>> = MutableLiveData()
    fun callEventDetail(token: String, eventId: String) {

        isLoading.postValue(true)
        isViewEnable.postValue(false)

        ApiClient().getClient().getEventDetail(token, eventId)
            .enqueue(object : Callback<EventDetailModel> {
                override fun onResponse(
                    call: Call<EventDetailModel>,
                    response: Response<EventDetailModel>) {

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

                override fun onFailure(call: Call<EventDetailModel?>, error: Throwable) {
                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    val errorMsg: String? = ErrorHandler.reportError(error)
                    resultData.value = WebResponse(Status.FAILURE, null, errorMsg)
                }
            })
    }


    //    call Event poll candidate API and get result in main thread
    var candidateResultData: MutableLiveData<WebResponse<PollCandidateModel>> = MutableLiveData()
    fun callEventPollCandidate(token: String, eventId: String) {

        isLoading.postValue(true)
        isViewEnable.postValue(false)

        ApiClient().getClient().getEventPollCandidate(token, eventId)
            .enqueue(object : Callback<PollCandidateModel> {
                override fun onResponse(
                    call: Call<PollCandidateModel>,
                    response: Response<PollCandidateModel>
                ) {

                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    if (response.isSuccessful && response.body() != null) {
                        val result = response.body()
                        result?.status?.let {
                            if (it) {
                                candidateResultData.value = WebResponse(Status.SUCCESS, result, null)
                            } else {

                                /*ToDo api give false status need to change in true*/
                                candidateResultData.value = WebResponse(Status.SUCCESS, result, null)

                                // candidateResultData.value = WebResponse(Status.FAILURE, null, result.message)
                            }
                        }
                    } else {
                        val errorMsg: String = ErrorHandler.reportError(response.code())
                        candidateResultData.value = WebResponse(Status.FAILURE, null, errorMsg)
                    }
                }

                override fun onFailure(call: Call<PollCandidateModel?>, error: Throwable) {
                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    val errorMsg: String? = ErrorHandler.reportError(error)
                    candidateResultData.value = WebResponse(Status.FAILURE, null, errorMsg)
                }
            })
    }



    //    call Event List API and get result in main thread
    var eventListResultData: MutableLiveData<WebResponse<EventModel>> = MutableLiveData()
    fun callEventList(token: String,filter:String) {

        isLoading.postValue(true)
        isViewEnable.postValue(false)

        ApiClient().getClient().getEventList(token,filter)
            .enqueue(object : Callback<EventModel> {
                override fun onResponse(
                    call: Call<EventModel>,
                    response: Response<EventModel>
                ) {

                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    if (response.isSuccessful && response.body() != null) {
                        val result = response.body()
                        result?.status?.let {
                            if (it) {
                                eventListResultData.value = WebResponse(Status.SUCCESS, result, null)
                            } else {
//                                eventListResultData.value = WebResponse(Status.SUCCESS, result, null)

                                eventListResultData.value = WebResponse(Status.FAILURE, null, result.message)
                            }
                        }
                    } else {
                        val errorMsg: String = ErrorHandler.reportError(response.code())
                        eventListResultData.value = WebResponse(Status.FAILURE, null, errorMsg)

                    }
                }

                override fun onFailure(call: Call<EventModel?>, error: Throwable) {
                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    val errorMsg: String? = ErrorHandler.reportError(error)
                    eventListResultData.value = WebResponse(Status.FAILURE, null, errorMsg)
                }
            })
    }



    //    call validate voter API and get result in main thread
    var voterValidateResultData: MutableLiveData<WebResponse<ValidateVoterModel>> = MutableLiveData()
    fun callVoterValidate(token: String,voterId:String) {

        isLoading.postValue(true)
        isViewEnable.postValue(false)

        ApiClient().getClient().getValidateVoter(token,voterId)
            .enqueue(object : Callback<ValidateVoterModel> {
                override fun onResponse(
                    call: Call<ValidateVoterModel>,
                    response: Response<ValidateVoterModel>
                ) {

                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    if (response.isSuccessful && response.body() != null) {
                        val result = response.body()
                        result?.status?.let {
                            if (it) {
                                voterValidateResultData.value = WebResponse(Status.SUCCESS, result, null)
                            } else {
                                voterValidateResultData.value = WebResponse(Status.FAILURE,  null,result.message)

                            }
                        }
                    } else {
                        val errorMsg: String = ErrorHandler.reportError(response.code())
                        voterValidateResultData.value = WebResponse(Status.FAILURE, null, errorMsg)

                    }
                }

                override fun onFailure(call: Call<ValidateVoterModel?>, error: Throwable) {
                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    val errorMsg: String? = ErrorHandler.reportError(error)
                    voterValidateResultData.value = WebResponse(Status.FAILURE, null, errorMsg)
                }
            })
    }


    //    call Vote Poll API and get result in main thread
    var pollVoteResultData: MutableLiveData<WebResponse<PollVoteModel>> = MutableLiveData()
    fun callPollVote(token: String,jsonArray: JsonArray) {

        isLoading.postValue(true)
        isViewEnable.postValue(false)

        ApiClient().getClient().postVotePoll(token,jsonArray)
            .enqueue(object : Callback<PollVoteModel> {
                override fun onResponse(
                    call: Call<PollVoteModel>,
                    response: Response<PollVoteModel>
                ) {

                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    if (response.isSuccessful && response.body() != null) {
                        val result = response.body()
                        result?.status?.let {
                            if (it) {
                                pollVoteResultData.value = WebResponse(Status.SUCCESS, result, null)
                            } else {
                                pollVoteResultData.value = WebResponse(Status.FAILURE,  null,result.message)

                            }
                        }
                    } else {
                        val errorMsg: String = ErrorHandler.reportError(response.code())
                        pollVoteResultData.value = WebResponse(Status.FAILURE, null, errorMsg)

                    }
                }

                override fun onFailure(call: Call<PollVoteModel?>, error: Throwable) {
                    isLoading.postValue(false)
                    isViewEnable.postValue(true)
                    val errorMsg: String? = ErrorHandler.reportError(error)
                    pollVoteResultData.value = WebResponse(Status.FAILURE, null, errorMsg)
                }
            })
    }
}