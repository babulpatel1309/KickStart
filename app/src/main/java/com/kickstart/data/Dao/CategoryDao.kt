package com.kickstart.data.Dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kickstart.data.Bean.WSCategoryBean

@Dao
interface CategoryDao {

    @Query("Select * from categories order by categoryOrder")
    fun getAllDetails(): LiveData<List<WSCategoryBean>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addItems(items: List<WSCategoryBean>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addItem(item: WSCategoryBean)

}