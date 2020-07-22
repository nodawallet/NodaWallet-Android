package com.application.nodawallet.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.application.nodawallet.model.History

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistory(history: History)

    @Query("SELECT * FROM trans_history")
    fun getallHistory(): LiveData<List<History>>

    @Delete
    fun deleteHistory(history: History)

}

