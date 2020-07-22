package com.application.nodawallet.model

data class BtcHistoryResponse(val status: String?, val data: BtcTxnData?, val code: Int?, val message: String?)

data class BtcTxnData(val balance:String?,val txs: List<TxnData>?)

data class Incoming(val output_no: Int?, val value: String?,val inputs: List<Outputs>?)

data class Outgoing(val value: String?,val outputs: List<Outputs>?)

data class Outputs(val output_no: Number?, val address: String?, val value: String?)

data class Inputs(val input_no: Number?, val address: String?, val value: String?)


data class TxnData(val txid: String?, val block_no: Number?, val confirmations: Number?, val time: Number?, val outgoing: Outgoing?, val incoming: Incoming?)
