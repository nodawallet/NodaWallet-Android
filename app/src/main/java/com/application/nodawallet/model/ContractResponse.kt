package com.application.nodawallet.model



data class ContractResponse(val status: Number?, val message: String?, val data: ContractData?)

data class ContractData(val admin_address: String?, val admin_private_key: String?, val contract_address: String?,
                        val infura_link: String?, val fee_address: String?)
