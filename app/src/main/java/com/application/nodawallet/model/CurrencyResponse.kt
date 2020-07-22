package com.application.nodawallet.model

import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.UtilsDefault


data class CurrencyResponse(val status: Int?, val message: String?, val data: List<GetCurrencyList>?)


data class GetCurrencyList(val currency: String?, val symbol: String?, val decimal: String?, val type:String?,
                           val gekko_id:String?, val logo: String?, val contract_address: String?,val price_change_percentage_24h_usd:Double,
                           val current_price_usd:Double,val price_change_percentage_24h_btc:Double,val current_price_btc:Double){



}

