package com.application.nodawallet.walletconnect.exceptions


import java.lang.Exception

class InvalidHmacException : Exception("Received and computed HMAC doesn't mach")