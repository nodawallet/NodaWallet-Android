package com.application.nodawallet.walletconnect

import com.application.nodawallet.walletconnect.models.WCPeerMeta
import com.application.nodawallet.walletconnect.models.binance.*
import com.application.nodawallet.walletconnect.models.ethereum.WCEthereumSignMessage
import com.application.nodawallet.walletconnect.models.ethereum.WCEthereumTransaction

/**
 * created by Alex Ivanov on 2019-06-14.
 */
interface WCCallbacks {

    /**
     * Notifies socket status update when callback is set, method triggers with current status
     */
    fun onStatusUpdate(status: Status)

    /**
     * Called when session request is asked from socket
     * Warning: Method called from
     */
    fun onSessionRequest(id: Long, peer: WCPeerMeta,peerId:String)

    fun ethSign(id: Long, message: WCEthereumSignMessage)

    fun ethSendTransaction(id: Long, transaction: WCEthereumTransaction)

    fun bnbsign(
        id: Long,
        order: WCBinanceTransferOrder
    )
    fun bnbTradePair(
        id: Long,
        order: WCBinanceTradePair
    )
    fun bnbTrade(
        id: Long,
        order: WCBinanceTradeOrder
    )
    fun bnbCancel(
        id: Long,
        order: WCBinanceCancelOrder
    )

    /**
     * Called when binance order signature required
     */

}