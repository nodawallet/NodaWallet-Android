package com.application.nodawallet.model

import androidx.annotation.Keep

@Keep
class InputParams {

    //exchange
    var address:String?=null
    var method:String?=null
    var type:String?=null
    var device:String?=null
    var firebase_id:String?=null

    //withdra
    var currency:String?=null
    var amount:String?=null
    var nonce:String?=null
    var gasprice:String?=null
    var gaslimit:String?=null

    //cms
    var page:String? = null

    //btcex
    var from_currency:String? = null
    var to_currency:String? = null
    var receive_address:String? = null






}