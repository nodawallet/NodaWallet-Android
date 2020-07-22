package com.application.nodawallet.model

data class VrsResponse(val status: Int?, val message: String?, val data: VRSData?)

data class VRSData(val message: String?, val messageHash: String?, val v: String?, val r: String?, val s: String?, val signature: String?)