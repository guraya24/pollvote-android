package com.pollvote.model.states

data class StatesModel(
    val message: String?,
    val code: Int?,
    val status: Boolean?,
    val data: MutableList<StateData?>
)