package com.kickstart.data.Bean

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TABLE_FinalScore")
data class ScoreBean(
        @PrimaryKey(autoGenerate = true)
        val _id: Int? = null,
        val _teams: String = "",
        val _rounds: String = "",
        val _score: String)