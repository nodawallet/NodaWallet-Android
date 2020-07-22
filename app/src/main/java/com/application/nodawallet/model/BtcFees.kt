package com.application.nodawallet.model


data class BtcFees(val name: String?, val height: Number?, val hash: String?, val time: String?, val latest_url: String?, val previous_hash: String?,
                   val previous_url: String?, val peer_count: Number?, val unconfirmed_count: Number?, val high_fee_per_kb: Long?,
                   val medium_fee_per_kb: Double?, val low_fee_per_kb: Long?, val last_fork_height: Number?, val last_fork_hash: String?)