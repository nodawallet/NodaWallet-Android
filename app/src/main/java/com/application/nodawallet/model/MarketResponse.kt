package com.application.nodawallet.model

data class MarketResponse(val id: String?, val symbol: String?, val name: String?, val image: String?,
                          val current_price: Double?,
                          val price_change_24h: Double?, val price_change_percentage_24h: Double?,
                          val ath_date: String?, val roi: Any?, val last_updated: String?)


