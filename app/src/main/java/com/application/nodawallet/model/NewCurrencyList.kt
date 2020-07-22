package com.application.nodawallet.model

data class NewCurrencyList(val currency: String?, val symbol: String?, val decimal: String?, val type:String?,
                           val gekko_id:String?, val logo: String?, val contract_address: String?,val price24h:Double,
                           val currentprice:Double)