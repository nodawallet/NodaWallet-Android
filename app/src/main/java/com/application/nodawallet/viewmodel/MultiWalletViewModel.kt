package com.application.nodawallet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.application.nodawallet.database.NodaDatabase
import com.application.nodawallet.model.MultiWalletList
import com.application.nodawallet.repository.MultiWalletRepository
import jnr.ffi.annotations.In
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class MultiWalletViewModel(nodaApplication: Application) : AndroidViewModel(nodaApplication) {

    private val repository: MultiWalletRepository
    val getAllData :LiveData<List<MultiWalletList>>

    init {

        val multidao = NodaDatabase.getDatabase(nodaApplication).walletDao()
        repository = MultiWalletRepository(multidao)
        getAllData =  repository.allData

    }

   fun insert(multiWalletList: MultiWalletList) = viewModelScope.launch {
       insertWallet(multiWalletList)

    }

    suspend fun insertWallet(multiWalletList: MultiWalletList) = withContext(Dispatchers.Default){
        repository.insert(multiWalletList)
    }

    fun deletItem(walletid:Int) = viewModelScope.launch {
        deletewalletId(walletid)
    }

    suspend fun deletewalletId(id: Int) = withContext(Dispatchers.Default){
        repository.deleteItem(id)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun update(name:String,id: Int) =viewModelScope.launch {
        updatewallet(name,id)
    }

    suspend fun updatewallet(name: String,id: Int) = withContext(Dispatchers.Default){
        repository.updateItem(name,id)
    }



    fun getDataById(id:Int)  = viewModelScope.launch {
        repository.getDataById()
    }

}