package com.kickstart.data.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kickstart.data.Bean.WSQuestionsBean

@Dao
interface WordsDao {

    @Query("Select * from questions order by id ")
    fun getAllDetails(): List<WSQuestionsBean>

    @Query("Select * from questions where catID=:catID order by id ")
    fun getAllDetails(catID: Int): List<WSQuestionsBean>

    @Query("Select * from questions where catID=:catID order by id ")
    fun getAllDetailsFiltered(catID: Int): LiveData<List<WSQuestionsBean>>

    @Query("Select * from questions where played=0 order by id ")
    fun getAllDetailsFiltered(): LiveData<List<WSQuestionsBean>>

    @Query("Select * from questions where id=:uniqueID LIMIT 1")
    fun checkRecord(uniqueID: Int): List<WSQuestionsBean>

    @Query("Select * from questions where id=:uniqueID LIMIT 1")
    fun getRecord(uniqueID: Int): WSQuestionsBean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addItems(transaction: List<WSQuestionsBean>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addItem(transaction: WSQuestionsBean): Long

    @Update
    fun updateItem(wsQuestionsBean: WSQuestionsBean)

    @Update
    fun updateItem(wsQuestionsBean: List<WSQuestionsBean>)

}