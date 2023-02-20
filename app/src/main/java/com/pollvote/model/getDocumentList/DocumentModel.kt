package com.pollvote.model.getDocumentList

data class DocumentModel(
    val message: String?,
    val status: Boolean?,
    val data: List<DocumentData>
)