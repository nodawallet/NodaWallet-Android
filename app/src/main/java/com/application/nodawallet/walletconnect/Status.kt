package com.application.nodawallet.walletconnect


sealed class Status {
    object DISCONNECTED : Status()
    object CONNECTED : Status()
    object CONNECTING : Status()
    object FAILED_CONNECT : Status()
}