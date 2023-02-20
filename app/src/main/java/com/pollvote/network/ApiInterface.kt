package com.pollvote.network


import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.pollvote.model.changeMobileNumber.ChangeMobileNoModel
import com.pollvote.model.changePassword.ChangePasswordModel
import com.pollvote.model.county.CountyModel
import com.pollvote.model.event.EventModel
import com.pollvote.model.eventDetail.EventDetailModel
import com.pollvote.model.getDocumentList.DocumentDeleteModel
import com.pollvote.model.getDocumentList.DocumentModel
import com.pollvote.model.getDocumentList.DocumentUploadedModel
import com.pollvote.model.login.CheckVoterModel
import com.pollvote.model.login.LoginModel
import com.pollvote.model.otpVerification.OTPVerificationModel
import com.pollvote.model.pollCandidate.PollCandidateModel
import com.pollvote.model.pollVote.PollVoteModel
import com.pollvote.model.profileDetail.ProfileImageModel
import com.pollvote.model.profileDetail.ProfileModel
import com.pollvote.model.register.RegisterModel
import com.pollvote.model.saveAddress.SaveAddressModel
import com.pollvote.model.states.StatesModel
import com.pollvote.model.velidateVoter.ValidateVoterModel


import okhttp3.MultipartBody

import retrofit2.Call
import retrofit2.http.*


interface ApiInterface {

    /*Login and SignUp*/
    @Headers(Constant.HEADER_ACCEPT, Constant.HEADER_CONTENT_TYPE)
    @POST(Constant.LOGIN)
    fun getLogin(
        @Body jsonBody: JsonObject
    ): Call<LoginModel>

    @Headers(Constant.HEADER_ACCEPT, Constant.HEADER_CONTENT_TYPE)
    @POST(Constant.CHECK_VOTER)
    fun checkVoter(
        @Body jsonBody: JsonObject
    ): Call<CheckVoterModel>

    @Headers(Constant.HEADER_CONTENT_TYPE)
    @POST(Constant.SIGNUP)
    fun getRegister(
        @Body jsonBody: JsonObject
    ): Call<RegisterModel>

    @Headers(Constant.HEADER_CONTENT_TYPE)
    @POST(Constant.SAVE_ADDRESS)
    fun postSaveAddress(
        @Header(Constant.HEADER_AUTHORIZATION) authHeader: String,
        @Body jsonBody: JsonObject
    ): Call<SaveAddressModel>


    @Headers(Constant.HEADER_CONTENT_TYPE)
    @POST(Constant.VALIDATE_OTP)
    fun getOTPVerify(
        @Body jsonBody: JsonObject
    ): Call<OTPVerificationModel>

    @Headers(Constant.HEADER_CONTENT_TYPE)
    @POST(Constant.RESEND_OTP)
    fun getResendOTP(
        @Body jsonBody: JsonObject
    ): Call<RegisterModel>


    @Headers(Constant.HEADER_CONTENT_TYPE)
    @POST(Constant.FORGOT_PASSWORD)
    fun getForgotPassword(
        @Body jsonBody: JsonObject
    ): Call<RegisterModel>


    @Headers(Constant.HEADER_CONTENT_TYPE)
    @GET(Constant.GET_STATES)
    fun getState(
        @Header(Constant.HEADER_AUTHORIZATION) authHeader: String,
        @Path("id") id: String
    ): Call<StatesModel>

    @Headers(Constant.HEADER_CONTENT_TYPE)
    @GET(Constant.GET_COUNTY)
    fun getCountyList(
        @Header(Constant.HEADER_AUTHORIZATION) authHeader: String,
        @Path("id") id: String
    ): Call<CountyModel>

    @Headers(Constant.HEADER_CONTENT_TYPE)
    @GET(Constant.GET_DOCUMENT)
    fun getDocumentList(
        @Header(Constant.HEADER_AUTHORIZATION) authHeader: String
    ): Call<DocumentModel>

    @POST(Constant.UPLOAD_DOCUMENT)
    @Multipart
    fun uploadDocument(
        @Header(Constant.HEADER_AUTHORIZATION) authHeader: String,
        @Path("id") id: Int,
        @Part image: MultipartBody.Part
    ): Call<DocumentUploadedModel>

    @Headers(Constant.HEADER_CONTENT_TYPE)
    @POST(Constant.DELETE_DOCUMENT)
    fun deleteDocument(
        @Header(Constant.HEADER_AUTHORIZATION) authHeader: String, @Path("id") id: Int
    ): Call<DocumentDeleteModel>


    /*Profile Detail*/
    @Headers(Constant.HEADER_CONTENT_TYPE)
    @GET(Constant.GET_PROFILE)
    fun getProfileDetail(
        @Header(Constant.HEADER_AUTHORIZATION) authHeader: String
    ): Call<ProfileModel>

    @Headers(Constant.HEADER_CONTENT_TYPE)
    @POST(Constant.CHANGE_PASSWORD)
    fun getChangePassword(
        @Header(Constant.HEADER_AUTHORIZATION) authHeader: String,
        @Body jsonBody: JsonObject
    ): Call<ChangePasswordModel>

    @Headers(Constant.HEADER_CONTENT_TYPE)
    @POST(Constant.CHANGE_PHONE)
    fun getChangeMobileNumber(
        @Header(Constant.HEADER_AUTHORIZATION) authHeader: String,
        @Body jsonBody: JsonObject
    ): Call<ChangeMobileNoModel>

    @Headers(Constant.HEADER_CONTENT_TYPE)
    @POST(Constant.CHANGE_PHONE_VERIFY_OTP)
    fun getChangeMobileNumberVerify(
        @Header(Constant.HEADER_AUTHORIZATION) authHeader: String,
        @Body jsonBody: JsonObject
    ): Call<ChangeMobileNoModel>



    @POST(Constant.UPLOAD_PROFILE_IMAGE)
    @Multipart
    fun uploadProfileImage(
        @Header(Constant.HEADER_AUTHORIZATION) authHeader: String,
        @Part image: MultipartBody.Part
    ): Call<ProfileImageModel>



    /*Event APIs*/
    @Headers(Constant.HEADER_CONTENT_TYPE)
    @GET(Constant.EVENT_LIST)
    fun getEventList(
        @Header(Constant.HEADER_AUTHORIZATION) authHeader: String,
        @Path("filter") filter: String
    ): Call<EventModel>

    @Headers(Constant.HEADER_CONTENT_TYPE)
    @GET(Constant.EVENT_DETAIL)
    fun getEventDetail(
        @Header(Constant.HEADER_AUTHORIZATION) authHeader: String,
        @Path("id") id: String
    ): Call<EventDetailModel>


    @Headers(Constant.HEADER_CONTENT_TYPE)
    @GET(Constant.EVENT_POLL_CANDIDATE)
    fun getEventPollCandidate(
        @Header(Constant.HEADER_AUTHORIZATION) authHeader: String,
        @Path("id") id: String
    ): Call<PollCandidateModel>


    @Headers(Constant.HEADER_CONTENT_TYPE)
    @GET(Constant.VALIDATE_VOTER)
    fun getEventValidateId(
        @Header(Constant.HEADER_AUTHORIZATION) authHeader: String,
        @Path("id") id: String
    ): Call<EventDetailModel>


    @Headers(Constant.HEADER_CONTENT_TYPE)
    @GET(Constant.VALIDATE_VOTER)
    fun getValidateVoter(
        @Header(Constant.HEADER_AUTHORIZATION) authHeader: String,
        @Path("id") id: String
    ): Call<ValidateVoterModel>


    @Headers(Constant.HEADER_CONTENT_TYPE)
    @POST(Constant.VOTE_POLL)
    fun postVotePoll(
        @Header(Constant.HEADER_AUTHORIZATION) authHeader: String,
        @Body jsonBody: JsonArray
    ): Call<PollVoteModel>

}