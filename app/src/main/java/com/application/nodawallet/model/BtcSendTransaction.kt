package com.application.nodawallet.model


data class BtcSendTransaction(val inputs: List<BTCInput>?, val outputs: List<BTCOutput>?,val confirmations:Int?,val preference:String?)

data class BTCInput(val addresses: List<String>?)

data class BTCOutput(val addresses: List<String>?, val value: Long?)
