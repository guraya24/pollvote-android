package com.pollvote.model.changeMobileNumber

data class ChangeMobileData(
val requestId:Int,
val mobileNumber:String,
val resendType:String,
val expireIn:Double
)