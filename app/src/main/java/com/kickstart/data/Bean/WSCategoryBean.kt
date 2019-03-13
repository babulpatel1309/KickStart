package com.kickstart.data.Bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "categories")
data class WSCategoryBean(

        @PrimaryKey(autoGenerate = true)
        @field:SerializedName("id")
        var id: Int? = null,

        @field:SerializedName("image")
        var image: String? = null,

        @field:SerializedName("bg_color")
        var bgColor: String? = null,

        @field:SerializedName("description")
        var description: String? = null,

        @field:SerializedName("is_paid")
        var catPaid: Int? = null,

        @field:SerializedName("category")
        var category: String? = null,

        @field:SerializedName("category_order")
        var categoryOrder: Int? = null,

        var favCategory: Int = 0 //0 :False 1: True
)
