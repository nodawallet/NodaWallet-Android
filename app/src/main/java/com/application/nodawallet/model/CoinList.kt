package com.application.nodawallet.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class CoinList(

    var coinName: String,
    var coinImage: Int,
    var tokenImage:String,
    var coinSymbol: String,
    var phrase: String,
    var coinType: String,
    var publicAddress: String,
    var privateKey: String,
    var publicKey: String,
    var enableStatus: Int,
    var balance: String,
    var marketPrice: String,
    var marketPercentage: String,
    var decimal:String,
    var network:String,
    var minex:Double,
    var maxex:Double,
    var fees:Double

)


