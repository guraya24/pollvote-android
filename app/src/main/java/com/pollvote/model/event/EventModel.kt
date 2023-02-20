package com.pollvote.model.event

data class EventModel(
    val message: String?,
    val status: Boolean?,
    val data: MutableList<EventListData>

)