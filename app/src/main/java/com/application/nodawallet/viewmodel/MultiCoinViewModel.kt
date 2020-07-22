package com.application.nodawallet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.application.nodawallet.database.NodaDatabase
import com.application.nodawallet.model.CoinList
import com.application.nodawallet.model.MultiCoinList
import com.application.nodawallet.repository.MultiCoinRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class MultiCoinViewModel (nodaApplication: Application) : AndroidViewModel(nodaApplication) {

    private val repository: MultiCoinRepository
    val getAllData : LiveData<List<MultiCoinList>>
    val getDataById : LiveData<List<MultiCoinList>>

    init {

        val multidao = NodaDatabase.getDatabase(nodaApplication).cointDao()
        repository = MultiCoinRepository(multidao)
        getAllData =  repository.allData
        getDataById =  repository.getDataById

    }

    fun insert(multiCoinList: MultiCoinList) = viewModelScope.launch {
        insertCoin(multiCoinList)

    }

    suspend fun insertCoin(multiCoinList: MultiCoinList) = withContext(Dispatchers.Default){
        repository.insert(multiCoinList)
    }


    fun deletItem(multiCoinList: MultiCoinList) = viewModelScope.launch {
        repository.deleteItem(multiCoinList)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun update(coinList: List<CoinList>,id: Int) =viewModelScope.launch {
        updatestatus(coinList,id)
    }

    suspend fun updatestatus(coinList: List<CoinList>,id:Int) = withContext(Dispatchers.Default){

        repository.updateItem(coinList,id)
    }

}