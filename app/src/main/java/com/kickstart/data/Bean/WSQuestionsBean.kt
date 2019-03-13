package com.kickstart.data.Bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "questions")
data class WSQuestionsBean(

        @PrimaryKey(autoGenerate = true)
        @field:SerializedName("id")
        var id: Int? = null,

        @field:SerializedName("question")
        var question: String? = null,

        @field:SerializedName("cat_id")
        var catID: Int? = null,

        var played: Int = 0 //0 : false 1: true
)
