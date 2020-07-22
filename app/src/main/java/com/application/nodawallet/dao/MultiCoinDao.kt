package com.application.nodawallet.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.application.nodawallet.model.CoinList
import com.application.nodawallet.model.MultiCoinList


@Dao
interface MultiCoinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMultiCoin(multiCoinList: MultiCoinList)


    @Query("SELECT * FROM multi_coin_list")
    fun getallData(): LiveData<List<MultiCoinList>>

    @Query("SELECT * FROM multi_coin_list WHERE id = :id")
    fun getDataById(id: Int): LiveData<List<MultiCoinList>>

    @Query("UPDATE multi_coin_list SET coinlist =:coinList  WHERE id =:id")
    fun updateById(coinList: List<CoinList>,id: Int)

    @Query("DELETE  FROM multi_coin_list")
    fun deleteAll()

    @Delete
    fun deleteItem(multiCoinList: MultiCoinList)
}

