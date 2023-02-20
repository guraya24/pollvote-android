package com.pollvote.model.register

data class RegistrationResponse(
    val requestId: Int?,
    //val firstName: String?,
    // val middleName: String?,
    // val lastName: String?,
    //val responsibleParty: String?,
    //val drivingLicense: String?,
    val mobileNumber: String?,
    // val password: String?,
    val deviceId: String?,
    // val deviceType: String?,
    // val fcmToken: String?,
    val resendType: String?,
    val expireIn: Double?



)