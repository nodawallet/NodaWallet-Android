package com.application.nodawallet.model

import androidx.room.*
import java.io.Serializable


@Entity(tableName = "multi_coin_list")
data class MultiCoinList (
    @PrimaryKey
    var id :Int=0,
    @TypeConverters(CoinTypeConverters::class)
    @ColumnInfo(name = "coinlist")
    var list:List<CoinList>

):Serializable
