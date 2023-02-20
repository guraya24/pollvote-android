package com.pollvote.model.candidateEvent

import com.google.gson.annotations.SerializedName

data class CandidatePositionResponse(

    @SerializedName("id")
    val id: String = "",

    @SerializedName("position_name")
    val positionName: String = "",

    @SerializedName("candidate_name")
    val list: List<CandidateNameResponse>



)