package com.application.nodawallet.walletconnect.models.session

import com.application.nodawallet.walletconnect.models.WCPeerMeta

data class WCSessionRequest(
    val peerId: String,
    val peerMeta: WCPeerMeta,
    val chainId: String?
)