package com.pollvote.model.login

import java.io.Serializable

data class UserSignupData(
    val createdDate: String?,
    val deviceid: String?,
    val devicetype: String?,
    val drivingLicence: String?,
    val email: String?,
    val fcmToken: String?,
    val firstName: String?,
    val id: Int?,
    val isVerified: Boolean?,
    val lastName: String?,
    val middleName: String?,
    val mobileNo: String?,
    val otp: String?,
    val otpexpire: String?,
    val otpstart: String?,
    val userPassword: String?,
    val voterId: String?
):Serializable