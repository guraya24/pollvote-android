package com.pollvote.model.login

data class CheckVoterModel(
    val message: String?,
    val status: Boolean?,
    val token: String?,
    val data:UserSignupData?,
    val code:Int?,
    val emailsendstatus:String?
)