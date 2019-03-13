package com.kickstart.data.Bean

import com.google.gson.annotations.SerializedName

data class CommonCategoryBean(

        @field:SerializedName("code")
        val code: Int? = null,
        @field:SerializedName("data")
        val data: List<WSCategoryBean>? = null
)
