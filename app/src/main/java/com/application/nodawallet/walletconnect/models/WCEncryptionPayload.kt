package com.application.nodawallet.walletconnect.models

data class WCEncryptionPayload(
    val data: String,
    val hmac: String,
    val iv: String
)