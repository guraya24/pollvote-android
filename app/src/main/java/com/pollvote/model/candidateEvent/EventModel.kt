package com.pollvote.model.candidateEvent

import com.google.gson.annotations.SerializedName

data class EventModel(

    @SerializedName("status")
    val id: String = "",

    @SerializedName("message")
    val title: String = "",

    @SerializedName("data")
    val list: List<CandidatePositionResponse>
)