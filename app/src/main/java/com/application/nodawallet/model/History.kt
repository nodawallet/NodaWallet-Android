package com.application.nodawallet.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.io.Serializable

@Entity(tableName = "trans_history")
data class History(@ColumnInfo(name = "history_type") var historyType: String,
                   @ColumnInfo(name = "send_amount") var sendAmount: String,
                   @ColumnInfo(name = "receive_amount") var receiveAmount: String,
                   @ColumnInfo(name = "send_currency") var sendCurrency: String,
                   @ColumnInfo(name = "receive_currency") var receiveCurrency: String,
                   @ColumnInfo(name = "fees") var fees: String,
                   @ColumnInfo(name = "ex_type") var exchangeType: String,
                   @ColumnInfo(name = "txn_hash") var txnHash: String,
                   @ColumnInfo(name = "gas_price") var gasPrice: String,
                   @ColumnInfo(name = "gas_limit") var gasLimit: String,
                   @ColumnInfo(name = "nonce") var nonce: String,
                   @ColumnInfo(name = "dateTime") var dateTime: String)
{
    @PrimaryKey(autoGenerate = true)
    var id :Int = 0
}

