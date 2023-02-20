package com.pollvote.model.otpVerification

data class OTPData(
    val id: Int?,
    val firstName: String?,
   // val middleName: String?,
   // val lastName: String?,
   // val responsibleParty: String?,
    val drivingLicense: String?,
    val mobileNo: String?,
    val email: String?,
  //  val deviceId: String?,
   // val deviceType: String?,
    val fcmToken: String?,
    val voterId: String?,
    val roleId: String?
)