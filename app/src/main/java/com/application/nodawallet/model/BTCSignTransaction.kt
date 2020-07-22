package com.application.nodawallet.model



data class BTCSignTransaction(val tx: Tx?, val tosign: List<String>?, val signatures: List<String>?, val pubkeys: List<String>?)
