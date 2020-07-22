package com.application.nodawallet.model


data class TokenInformation(val status: String?, val message: String?, val result: List<TokenList>?)

data class TokenList(val blockNumber: String?, val timeStamp: String?, val hash: String?,
                           val nonce: String?, val blockHash: String?, val from: String?,
                           val contractAddress: String?, val to: String?, val value: String?,
                           val tokenName: String?, val tokenSymbol: String?, val tokenDecimal: String?,
                           val transactionIndex: String?, val gas: String?, val gasPrice: String?,
                           val gasUsed: String?, val cumulativeGasUsed: String?, val input: String?,
                           val confirmations: String?)
