package com.application.nodawallet.repository

import androidx.lifecycle.LiveData
import com.application.nodawallet.dao.MultiCoinDao
import com.application.nodawallet.model.CoinList
import com.application.nodawallet.model.MultiCoinList
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.UtilsDefault
import jnr.ffi.annotations.In

class MultiCoinRepository (private val multiCoinDao: MultiCoinDao) {

    val coinId = UtilsDefault.getSharedPreferenceInt(Constants.COINID)

    val allData: LiveData<List<MultiCoinList>> = multiCoinDao.getallData()
    val getDataById: LiveData<List<MultiCoinList>> = multiCoinDao.getDataById(coinId)

    fun insert(MultiCoinList: MultiCoinList){
        multiCoinDao.insertMultiCoin(MultiCoinList)
    }

    fun deleteItem(MultiCoinList: MultiCoinList){
        multiCoinDao.deleteItem(MultiCoinList)
    }

    fun deleteAll(){
        multiCoinDao.deleteAll()
    }

    fun updateItem(coinList: List<CoinList>,id:Int){
        multiCoinDao.updateById(coinList,id)
    }
    




}