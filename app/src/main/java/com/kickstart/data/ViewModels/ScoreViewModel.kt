package com.kickstart.data.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kickstart.ApplicationClass
import com.kickstart.data.Bean.ScoreBean
import com.kickstart.data.Database.RoomDBHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ScoreViewModel : ViewModel() {

    var userList: LiveData<List<ScoreBean>>
    var roomDBHelper: RoomDBHelper

    init {
        roomDBHelper = RoomDBHelper.getInstance(ApplicationClass.mInstance)
        userList = roomDBHelper.userDao().getAllDetails()
    }


    fun getAllDetails(): LiveData<List<ScoreBean>> {
        return userList
    }

    fun addItems(users: List<ScoreBean>) {
        GlobalScope.launch {
            roomDBHelper.userDao().addItems(users)
        }
    }

    fun addItem(user: ScoreBean) {
        GlobalScope.launch {
            roomDBHelper.setTransactionSuccessful()
        }
    }

}