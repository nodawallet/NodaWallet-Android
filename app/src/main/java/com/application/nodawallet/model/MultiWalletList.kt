package com.application.nodawallet.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "multi_wallet_list")
data class MultiWalletList (@ColumnInfo(name = "wallet_name") var walletName: String,
                            @ColumnInfo(name = "wallet_image") var walletImage: Int,
                            @ColumnInfo(name = "wallet_type") var walletType: Int,
                            @ColumnInfo(name = "phrase") var phrase: String,
                            @ColumnInfo(name = "import_type") var importType: String)
{
    @PrimaryKey(autoGenerate = true)
    var id :Int = 0
}
