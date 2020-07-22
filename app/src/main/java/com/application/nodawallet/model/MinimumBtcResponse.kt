package com.application.nodawallet.model

data class MinimumBtcResponse(val status: Int?, val message: String?, val data: MinimumData?)

data class MinimumData(val jsonrpc: String?, val id: String?, val result: String?,val error: Error?)

data class Error(val code: Number?, val message: String?)

