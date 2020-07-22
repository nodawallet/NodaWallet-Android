package com.application.nodawallet.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.application.nodawallet.model.MultiWalletList

@Dao
interface MultiWalletDao {

    @Insert(onConflict = REPLACE)
    fun insertMultiWallet(multiWalletList: MultiWalletList):Long

    @Query("SELECT * FROM multi_wallet_list")
    fun getallData():LiveData<List<MultiWalletList>>

    @Query("SELECT * FROM multi_wallet_list")
    fun getDataById():LiveData<MultiWalletList>

    @Query("UPDATE multi_wallet_list SET wallet_name = :name  WHERE id =:id")
    fun updateById(name:String,id:Int)

    @Query("DELETE  FROM multi_wallet_list")
    fun deleteAll()

    @Query("DELETE FROM multi_wallet_list WHERE id = :walletId" )
    fun deleteItem(walletId: Int)


}