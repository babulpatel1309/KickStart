package com.kickstart.data.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kickstart.ApplicationClass
import com.kickstart.data.Bean.WSCategoryBean
import com.kickstart.data.Database.RoomDBHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {

    var userList: LiveData<List<WSCategoryBean>>
    var roomDBHelper: RoomDBHelper

    init {
        roomDBHelper = RoomDBHelper.getInstance(ApplicationClass.mInstance)
        userList = roomDBHelper.categoryDao().getAllDetails()
    }


    fun getAllDetails(): LiveData<List<WSCategoryBean>> {
        return userList
    }

    fun addItems(users: List<WSCategoryBean>) {
        GlobalScope.launch {
            roomDBHelper.categoryDao().addItems(users)
        }
    }

    fun addItem(user: WSCategoryBean) {
        GlobalScope.launch {
            roomDBHelper.categoryDao().addItem(user)
        }
    }

}