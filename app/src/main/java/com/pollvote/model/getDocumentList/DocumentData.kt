package com.pollvote.model.getDocumentList

data class DocumentData(
    var id: Int?,
    val documentName: String?,
    var isUploaded:Boolean?,
    var filePath: String = ""
)