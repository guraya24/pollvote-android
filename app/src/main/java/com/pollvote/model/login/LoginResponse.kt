package com.pollvote.model.login

data class LoginResponse(


    val firstName: String = "",
    // val middleName: String,
    //val lastName: String,
    // val username: String,
    val drivingLicense: String = "",
    val mobileNo: String = "",
    val email: String = "",
    val id: Int = 0,
    val voterId: String = "",
    val isAddressAvailable: Boolean = false,
    val logofile:String,
    val address: AddressResponse?
)