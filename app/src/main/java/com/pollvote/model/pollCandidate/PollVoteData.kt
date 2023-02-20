package com.pollvote.model.pollCandidate

data class PollVoteData(

    val id: Int?,
    val pollName: String?,
    val eventName: String?,
    val eventId: Int?,
    val candidateList: MutableList<PollVoteCandidateList>
)