package com.pollvote.model.otpVerification

data class OTPVerificationModel(
    val message: String?,
    val status: Boolean?,
    val token:String?,
    val data: OTPData?
)