package org.bxkr.octodiary.models.govexams


import com.google.gson.annotations.SerializedName

data class GovExamsResponse(
    @SerializedName("data")
    val `data`: List<Exam>,
    @SerializedName("result")
    val result: String,
)