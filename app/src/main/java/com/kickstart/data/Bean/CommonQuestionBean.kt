package com.kickstart.data.Bean

import com.google.gson.annotations.SerializedName

data class CommonQuestionBean(

        @field:SerializedName("code")
        val code: Int? = null,

        @field:SerializedName("data")
        val data: List<WSQuestionsBean>? = null,

        @field:SerializedName("message")
        val message: String? = null
)
