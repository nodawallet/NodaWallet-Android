package com.application.nodawallet.model

data class TransactionHistory(val hash: String?, val timeStamp:String?,val nonce: String?, val tokenDecimal:String,val contractAddress:String,
                              val from: String?, val to: String?, val value: String?, val gasPrice: String?,val isError:String?,val gasUsed:String,val confirmations:String?,val input:String?)
