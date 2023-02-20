package com.pollvote.model.getDocumentList

data class DocumentUploadedModel(
    val code: Int?,
    val data: Data?,
    val emailsendstatus: String?,
    val message: String?,
    val otp: String?,
    val status: Boolean?,
    val token: String?
){
    data class Data(
        val document: Any?,
        val documentFileName: String?,
        val documentId: Int?,
        val id: Int?,
        val isDeleted: Boolean?,
        val updatedOn: String?,
        val user: Any?,
        val userId: Int?
    )
}