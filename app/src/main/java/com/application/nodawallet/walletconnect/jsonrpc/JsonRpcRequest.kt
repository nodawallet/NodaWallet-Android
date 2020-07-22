package com.application.nodawallet.walletconnect.jsonrpc

import com.application.nodawallet.walletconnect.JSONRPC_VERSION
import com.application.nodawallet.walletconnect.models.WCMethod

data class JsonRpcRequest<T>(
    val id: Long,
    val jsonrpc: String = JSONRPC_VERSION,
    val method: WCMethod?,
    val params: T
)