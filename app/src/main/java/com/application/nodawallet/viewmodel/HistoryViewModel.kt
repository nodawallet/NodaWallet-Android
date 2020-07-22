package com.application.nodawallet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.application.nodawallet.database.NodaDatabase
import com.application.nodawallet.model.History
import com.application.nodawallet.model.MultiCoinList
import com.application.nodawallet.repository.HistoryRepository
import com.application.nodawallet.repository.MultiCoinRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class HistoryViewModel (nodaApplication: Application) : AndroidViewModel(nodaApplication)  {

    private val repository: HistoryRepository
    val getAllData : LiveData<List<History>>

    init {

        val historydao = NodaDatabase.getDatabase(nodaApplication).historydao()
        repository = HistoryRepository(historydao)
        getAllData =  repository.allData

    }

    fun insert(history: History) = viewModelScope.launch {
        inserthistory(history)

    }

    suspend fun inserthistory(history: History) = withContext(Dispatchers.Default){
        repository.insert(history)
    }


    fun deletItem(history: History) = viewModelScope.launch {
        repository.deleteItem(history)
    }

}