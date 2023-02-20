package com.pollvote.model.getDocumentList

data class DocumentDeleteModel(
    val code: Int?,
    val `data`: String?,
    val emailsendstatus: String?,
    val message: String?,
    val otp: String?,
    val status: Boolean?,
    val token: String?
)