package com.application.nodawallet.model


data class BTCSendResponse(val tx: Tx?, val tosign: List<String>?)

data class Inputs1374035522(val prev_hash: String?, val output_index: Long?, val output_value: Long?, val sequence: Long?, val addresses: List<String>?, val script_type: String?, val age: Long?)

data class Outputs1677603245(val value: Long?, val script: String?, val addresses: List<String>?, val script_type: String?)

data class Tx(val block_height: Long?, val block_index: Long?, val hash: String?, val addresses: List<String>?, val total: Long?, val fees: Long?, val size: Long?, val preference: String?, val relayed_by: String?, val received: String?, val ver: Long?, val double_spend: Boolean?, val vin_sz: Long?, val vout_sz: Long?, val confirmations: Long?, val inputs: List<Inputs1374035522>?, val outputs: List<Outputs1677603245>?)
