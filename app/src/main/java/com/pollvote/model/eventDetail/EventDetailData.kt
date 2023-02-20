package com.pollvote.model.eventDetail

data class EventDetailData(
    var id: Int?,
    var eventName: String?,
    var scheduleDate: String?,
    // var scheduleEndDate: String,
    //var eventTypeName: String,
    val organizerName: String?,
    val address: String?,
    val startTime: EventDetailStartTime?,
    val stopTime: EventDetailEndTime?,
    val pollList: MutableList<EventDetailPollList>
)