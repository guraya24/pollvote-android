package com.pollvote.model.pollCandidate

data class PollCandidateModel(
    val message: String?,
    val status: Boolean?,
    val data: MutableList<PollVoteData>
)