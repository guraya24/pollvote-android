package com.pollvote.model.saveAddress

data class SaveAddressData(
val id: Int?,
val userId: Int?,
val addressLine1: String?,
val addressLine2: String?,
val twonShip: String?,
val city: String?,
val countyId: Int?,
val stateId: Int?,
val countryId: Int?,
val zipCode: String?,
val gender: String?,
val dob: String?
)