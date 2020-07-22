package com.application.nodawallet.repository

import androidx.lifecycle.LiveData
import com.application.nodawallet.dao.MultiWalletDao
import com.application.nodawallet.model.MultiWalletList

class MultiWalletRepository(private val multiWalletDao:MultiWalletDao) {


    val allData:LiveData<List<MultiWalletList>> = multiWalletDao.getallData()

   fun insert(multiWalletList: MultiWalletList){
        multiWalletDao.insertMultiWallet(multiWalletList)

    }

    fun deleteItem(walletid:Int){
        multiWalletDao.deleteItem(walletid)
    }

    fun deleteAll(){
        multiWalletDao.deleteAll()
    }

    fun updateItem(name:String,id:Int){
        multiWalletDao.updateById(name,id)
    }

    fun getDataById(){
        multiWalletDao.getDataById()
    }





}