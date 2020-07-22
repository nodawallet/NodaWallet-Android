package com.application.nodawallet.model



data class ETHExplorer(val address: String?, val ETH: ETH?, val tokens: List<TokensBalance>?)

data class ETH(val balance: Double?)
data class TokensBalance( val balance: Double?,val tokenInfo: TokenInfo?)
data class TokenInfo(val symbol: String?,val decimals:String)
