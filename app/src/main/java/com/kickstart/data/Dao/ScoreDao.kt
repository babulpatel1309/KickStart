package com.kickstart.data.Dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kickstart.data.Bean.ScoreBean

@Dao
interface ScoreDao {

    @Query("Select * from table_finalscore")
    fun getAllDetails(): LiveData<List<ScoreBean>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addItems(users: List<ScoreBean>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addItem(user: ScoreBean)

}