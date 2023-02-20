package com.pollvote.model.county

data class CountyModel(
    val message: String?,
    val code: Int?,
    val status: Boolean?,
    val data: MutableList<CountyData?>
)