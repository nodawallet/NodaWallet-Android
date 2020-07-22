package com.application.nodawallet.repository

import androidx.lifecycle.LiveData
import com.application.nodawallet.dao.HistoryDao
import com.application.nodawallet.model.History
import com.application.nodawallet.model.MultiCoinList

class HistoryRepository(private val historyDao: HistoryDao) {

    val allData: LiveData<List<History>> = historyDao.getallHistory()

    fun insert(history: History){
        historyDao.insertHistory(history)
    }

    fun deleteItem(history: History){
        historyDao.deleteHistory(history)
    }
}