package com.application.nodawallet.walletconnect.models

import com.application.nodawallet.walletconnect.models.MessageType

data class WCSocketMessage(
    val topic: String,
    val type: MessageType,
    val payload: String
)