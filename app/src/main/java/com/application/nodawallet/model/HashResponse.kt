package com.application.nodawallet.model


data class HashResponse(val status: String?, val data: HashData?)

data class HashData(val network: String?, val txid: String?, val blockhash: String?, val confirmations: Number?, val time: Number?, val inputs: List<InputHash>?, val outputs: List<OutputHash>?, val tx_hex: String?, val size: Number?, val version: Number?, val locktime: Number?)

data class From_output(val txid: String?, val output_no: Number?)

data class InputHash(val input_no: Number?, val value: String?, val address: String?, val type: String?, val script: String?, val witness: Any?, val from_output: From_output?)

data class OutputHash(val output_no: Number?, val value: String?, val address: String?, val type: String?, val script: String?)
