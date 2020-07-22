package com.application.nodawallet.walletconnect.exceptions

import java.lang.Exception

class InvalidJsonRpcParamsException(val requestId: Long) : Exception("Invalid JSON RPC Request")
