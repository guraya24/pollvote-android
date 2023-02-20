package com.pollvote.model.event

data class EventListData(
    var id: Int?,
    var eventName: String?,
    var scheduleDate: String?,
    var scheduleEndDate: String?,
    var eventTypeName: String?,
    var addressLine1: String?,
    var isEligible: Boolean?,
    var hasVoted: Boolean?,
    val requiredDocuments: String?,
    val township: String,
    val city: String,
    val zipCode: String,
    val country: String,
    val state: String,
    val county: String,
    val documentList: List<EventDocumentList>
)

