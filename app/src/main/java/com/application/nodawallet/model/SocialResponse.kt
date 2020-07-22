package com.application.nodawallet.model


data class SocialResponse(val status: Int?, val message: String?, val data: Data?)

data class Data(val twitter: String?, val telegram: String?)

