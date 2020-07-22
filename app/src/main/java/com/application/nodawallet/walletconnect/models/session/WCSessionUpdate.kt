package com.application.nodawallet.walletconnect.models.session

data class WCSessionUpdate(
    val approved: Boolean,
    val chainId: Int?,
    val accounts: List<String>?
)