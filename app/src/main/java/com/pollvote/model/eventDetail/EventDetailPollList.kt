package com.pollvote.model.eventDetail

import com.pollvote.model.pollCandidate.PollVoteCandidateList

data class EventDetailPollList(
    val id:Int?,
    val pollName:String?,
    val eventName:String?,
    val eventId:Int?,
    val candidateList:List<PollVoteCandidateList>,
    var selectCandidateName:PollVoteCandidateList?= PollVoteCandidateList()
)