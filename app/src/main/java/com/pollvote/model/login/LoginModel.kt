package com.pollvote.model.login

data class LoginModel(
    val message: String?,
    val status: Boolean?,
    val token: String?,
    val data:LoginResponse?
)