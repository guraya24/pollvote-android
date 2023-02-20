package com.pollvote.model.profileDetail

data class ProfileData(
    val id: Int?,
    val firstName: String?,
    val middleName: String?,
    val lastName: String?,
    val username: String?,
    val drivingLicense: String?,
    val address1: String?,
    val address2: String?,
    val city: String?,
    val zipcode: String?,
    val townShip: String?,
    val country: String?,
    val stateid: String?,
    val countyId: String?,
    val mobileNo: String?,
    val landLineNumber: String?,
    val email: String?,
    val logofile: String?,

    val status: Boolean?,
    val voterId: String?,
    val rememberMe: Boolean?,
    val countryId: String?,
    val stateName: String?,
    val county: String?,
    val dobString: String?,
    val dob: String?,
    val gender: String?
)