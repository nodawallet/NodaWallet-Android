package com.application.nodawallet.model


data class BNBFees(val fixed_fee_params: FixedFees?)

data class FixedFees(val msg_type: String?, val fee: Double?, val fee_for: Int?)
