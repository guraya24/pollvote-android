package com.pollvote.network

class Constant {
    companion object {

        /*Error Values*/
        const val NO_INTERNET = 101


        /*API Headers*/
        const val HEADER_ACCEPT = "Accept: application/json"
        const val HEADER_CONTENT_TYPE = "Content-Type: application/json"
        const val HEADER_AUTHORIZATION = "Authorization"

        const val mobile = "MobileNo"
        const val email = "Email"
        const val ExpireIn = "expireIn"
        const val RequestId = "requestId"
        const val ResendType = "resendType"

        const val newMobile = "newMobile"
        const val oldMobile = "oldMobile"
        const val isMobileChanged = "isMobileChanged"

        //        Auth APIs
        const val LOGIN = "api/login"

        //USER APIS
        const val SIGNUP = "api/user/signup"
        const val SAVE_ADDRESS = "api/user/saveaddress"
        const val VALIDATE_OTP = "api/user/validateotp"
        const val RESEND_OTP = "api/user/resendotp"
        const val FORGOT_PASSWORD = "/api/user/forgotpassword"


        const val UPLOAD_DOCUMENT = "api/user/uploaddocument/{id}"
        const val GET_DOCUMENT = "api/user/getdocument"
        const val DELETE_DOCUMENT = "/api/user/deletedocument/{id}"
        const val CHECK_VOTER = "api/user/checkvoter"
        const val CHANGE_PASSWORD = "api/user/changepassword"
        const val CHANGE_PHONE = "api/user/changephone"
        const val CHANGE_PHONE_VERIFY_OTP = "api/user/changephoneverifyotp"
        const val GET_PROFILE = "api/user/getprofile"
        const val UPLOAD_PROFILE_IMAGE = "api/user/uploadprofilephoto"

        //EVENT APIS
        const val EVENT_LIST = "api/Event/EventList/{filter}"
        const val EVENT_DETAIL = "api/Event/EventDetail/{id}/"
        const val EVENT_POLL_CANDIDATE = "api/poll/gettpollandcandidates/{id}/"


        //POLL API
        const val VALIDATE_VOTER = "api/poll/validatevoter/{id}/"
        const val VOTE_POLL = "api/poll/vote"

        //COMMON APIS
        const val GET_STATES = "api/common/getstates/{id}/"
        const val GET_COUNTY = "api/common/getcounty/{id}/"


    }
}