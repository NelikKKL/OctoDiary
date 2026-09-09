package org.bxkr.octodiary.models.marklistsubjectshort

import com.google.gson.annotations.SerializedName

data class MarkListSubject(
    @SerializedName("payload")
    val payload: List<MarkListSubjectItem>,
)
