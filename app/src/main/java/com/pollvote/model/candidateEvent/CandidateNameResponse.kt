package com.pollvote.model.candidateEvent

import com.google.gson.annotations.SerializedName

data class CandidateNameResponse(

    @SerializedName("id")
    val id: String = "",

    @SerializedName("name")
    val name: String = "",
    @SerializedName("party")
    val party: String = ""
)