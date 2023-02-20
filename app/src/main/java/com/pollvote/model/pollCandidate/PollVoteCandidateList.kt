package com.pollvote.model.pollCandidate

data class PollVoteCandidateList(
    val id: Int?,
    val eventId: Int?,
    val pollId: Int?,
    val firstName: String?,
    val middleName: String?,
    val lastName: String?,
    val partyAffiliation: String?
) { constructor() : this(
        0, 0, 0,
        "", "", "", ""
    ) {
    }
}