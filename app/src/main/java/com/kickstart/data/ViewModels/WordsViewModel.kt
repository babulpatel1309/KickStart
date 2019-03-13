package com.kickstart.data.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kickstart.ApplicationClass
import com.kickstart.data.Bean.WSQuestionsBean
import com.kickstart.data.Database.RoomDBHelper
import com.kickstart.utils.OperationResponder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class WordsViewModel : ViewModel() {

    var transactionList: LiveData<List<WSQuestionsBean>>
    var roomDBHelper: RoomDBHelper

    init {
        roomDBHelper = RoomDBHelper.getInstance(ApplicationClass.mInstance)
        transactionList = roomDBHelper.transactionsDao().getAllDetailsFiltered()
    }

    fun getAllDetail(): LiveData<List<WSQuestionsBean>> {
        return transactionList
    }

    fun getAllDetail(catID: Int): LiveData<List<WSQuestionsBean>> {
        return roomDBHelper.transactionsDao().getAllDetailsFiltered(catID)
    }

    fun getAllQuestions(catID: Int, operationResponder: OperationResponder? = null) {
        GlobalScope.async {
            var list: List<WSQuestionsBean>? = null
            val result = GlobalScope.launch {
                list = roomDBHelper.transactionsDao().getAllDetails(catID)
                return@launch
            }
            result.join()
            operationResponder?.OnComplete(list)
        }
    }

    fun getTransaction(uniqueID: Int, operationResponder: OperationResponder? = null) {
        GlobalScope.launch {
            operationResponder?.OnComplete(roomDBHelper.transactionsDao().getRecord(uniqueID))
        }
    }

    fun updateQuestions(wsQuestionsBean: WSQuestionsBean, operationResponder: OperationResponder? = null) {
        GlobalScope.launch {
            operationResponder?.OnComplete(roomDBHelper.transactionsDao().updateItem(wsQuestionsBean))
        }
    }

    fun updateQuestions(wsQuestionsBean: List<WSQuestionsBean>, operationResponder: OperationResponder? = null) {
        GlobalScope.launch {

            operationResponder?.OnComplete(roomDBHelper.transactionsDao().updateItem(wsQuestionsBean))
        }
    }

    fun addItems(transaction: List<WSQuestionsBean>) {
        GlobalScope.launch {

            transaction.forEach {
                val record = roomDBHelper.transactionsDao().checkRecord(it.id!!)
                if (record.isEmpty())
                    roomDBHelper.transactionsDao().addItem(it)
            }
        }
    }

    suspend fun addItems(transaction: WSQuestionsBean): Long {
        return GlobalScope.async { return@async roomDBHelper.transactionsDao().addItem(transaction) }.await()
    }

}