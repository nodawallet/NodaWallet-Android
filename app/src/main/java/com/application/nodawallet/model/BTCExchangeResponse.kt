package com.application.nodawallet.model



data class BTCExchangeResponse(val status: Int?, val message: String?, val data: DepositData?)

data class DepositData(val deposit_address: String?, val deposit_amount: String?,val extra_id:String?)
