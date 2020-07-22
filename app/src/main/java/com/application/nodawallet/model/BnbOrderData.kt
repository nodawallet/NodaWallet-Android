package com.application.nodawallet.model


data class BnbOrderData(val orderData: OrderData?)

data class OrderData(val symbol: String?, val side: String?, val memo: String?, val source: Int?, val sequence: Int?)