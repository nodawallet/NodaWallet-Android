package com.application.nodawallet.activity

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import com.application.nodawallet.R
import com.application.nodawallet.application.NodaApplication
import com.application.nodawallet.model.WCBinanceOrderSignature
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.UtilsDefault
import com.application.nodawallet.walletconnect.*
import com.application.nodawallet.walletconnect.models.WCPeerMeta
import com.application.nodawallet.walletconnect.models.binance.WCBinanceCancelOrder
import com.application.nodawallet.walletconnect.models.binance.WCBinanceTradeOrder
import com.application.nodawallet.walletconnect.models.binance.WCBinanceTradePair
import com.application.nodawallet.walletconnect.models.binance.WCBinanceTransferOrder
import com.application.nodawallet.walletconnect.models.ethereum.WCEthereumSignMessage
import com.application.nodawallet.walletconnect.models.ethereum.WCEthereumTransaction
import com.application.nodawallet.walletconnect.models.session.WCSession
import com.binance.dex.api.client.BinanceDexEnvironment
import com.binance.dex.api.client.Wallet
import com.binance.dex.api.client.domain.OrderSide
import com.binance.dex.api.client.domain.OrderType
import com.binance.dex.api.client.domain.TimeInForce
import com.binance.dex.api.client.domain.broadcast.NewOrder
import com.binance.dex.api.client.domain.broadcast.TransactionOption
import com.binance.dex.api.client.encoding.Crypto
import com.binance.dex.api.client.encoding.EncodeUtils
import com.binance.dex.api.client.encoding.message.BinanceDexTransactionMessage
import com.binance.dex.api.client.encoding.message.NewOrderMessage
import com.binance.dex.api.client.encoding.message.SignData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.paytomat.binance.Signature
import com.paytomat.btc.PrivateKey
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_wallet_connect.*
import org.bitcoinj.crypto.DeterministicKey
import org.bitcoinj.crypto.HDKeyDerivation
import org.bitcoinj.crypto.HDUtils
import org.bitcoinj.crypto.MnemonicCode
import org.spongycastle.util.encoders.Hex
import org.web3j.crypto.Credentials
import org.web3j.utils.Convert
import org.web3j.utils.Numeric
import java.io.IOException
import java.security.NoSuchAlgorithmException

class WalletConnectActivity : BaseActivity(), WCCallbacks {

    var qrcode = ""
    var wcClient: WCClient? = null
    val key = UtilsDefault.getSharedPreferenceString(Constants.PHRASE)
    val mnemonic = UtilsDefault.getSharedPreferenceString(Constants.SEED)
    private val handler: Handler = Handler(Looper.getMainLooper())
    private var sharedPreferences = NodaApplication.instance.getSharedPreferences("wallet", Context.MODE_PRIVATE)
    private val storage =  WCSessionStoreType(sharedPreferences!!)
    companion object {
        const val SESSION_KEY = "org.walletconnect.session"
        var status = false
    }

    val end = UtilsDefault.getSharedPreferenceString(Constants.BNBKEY)
    val wallet = Wallet(end, BinanceDexEnvironment.PROD)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_connect)

        if (sharedPreferences.getString(SESSION_KEY,null)!= null){

            textWalletName?.visibility = View.VISIBLE
            btnDisconnect?.visibility = View.VISIBLE
            textAdd?.visibility = View.VISIBLE
            textConnect?.visibility = View.VISIBLE
            textWalletName?.text = storage.session!!.remotePeerMeta.name
            textUrl?.text = storage.session!!.remotePeerMeta.url

            if (storage.session!!.remotePeerMeta.url.contains("binance")){
                textAddress?.text = wallet.address
            }
            else {
                val credentials = Credentials.create(key)
                textAddress?.text = credentials.address
            }


            try {
                Picasso.with(this@WalletConnectActivity).load(storage.session!!.remotePeerMeta.icons[0]).into(imgWallet)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            wcClient = WCClient()
            wcClient?.callbacks = this
            wcClient?.connect(storage.session!!.session, storage.session!!.remotePeerMeta, storage.session!!.peerId, storage.session!!.remotePeerId)

        }
        else {
            startActivityForResult(Intent(this, ScanActivity::class.java), 101)

        }


        imgBackWallet?.setOnClickListener {
            leaveSession()
        }


        btnDisconnect?.setOnClickListener {
            leaveSession()
        }

    }

    fun leaveSession() {
        MaterialAlertDialogBuilder(this, R.style.MyAlertDialog).apply {
            setMessage(getString(R.string.leave_session))
            setPositiveButton(getString(R.string.saveExit)) { dialogInterface: DialogInterface, i: Int ->

                finish()
                dialogInterface.dismiss()

            }
            setNegativeButton(getString(R.string.ok)) { dialogInterface: DialogInterface, i: Int ->
                storage.session = null
                wcClient?.killSession()
                wcClient?.disconnect()
                finish()
                dialogInterface.dismiss()
            }
        }.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {

            if (data != null) {
                val value = data.getStringExtra("barcode")
                qrcode = value!!
                if (!qrcode.contains("bridge")) {
                    Toast.makeText(
                        this,
                        getString(R.string.qrcode_invalid_wallet),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }

            if (qrcode != "") {

                if (wcClient != null) {
                    wcClient?.killSession()
                    wcClient = null
                } else {

                    val sessionStr: String = qrcode
                    wcClient = WCClient()
                    val clientMeta = WCPeerMeta(
                        "Wallet Connect",
                        "https://walletconnect.com/"
                    )
                    Log.d("qrcode",qrcode)
                    val session = WCSession.from(sessionStr) ?: return
                    val uuid = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                    wcClient?.callbacks = this
                    wcClient?.connect(session, clientMeta, uuid, "54")


                }
            }
        }

    }

    override fun onStatusUpdate(status: Status) {
        when (status) {
            Status.CONNECTED -> Log.d("status", "connected")
            Status.FAILED_CONNECT -> Log.d("status", "Failed to connect")
            Status.CONNECTING -> Log.d("status", "Connecting")
            Status.DISCONNECTED -> {
                storage.session = null
                wcClient?.killSession()
                wcClient?.disconnect()
                finish()
            }

        }

    }

    override fun onSessionRequest(id: Long, peer: WCPeerMeta,peerId:String) {

        if (!isDestroyed && !isFinishing){

            handler.post {

                Log.d("<<SS", "Show alert id: $id peer: $peer")
                val url = peer.url
                MaterialAlertDialogBuilder(this, R.style.MyAlertDialog).apply {
                    setMessage(getString(R.string.confirm_session) + "" + url)
                    setPositiveButton(getString(R.string.approve)) { dialogInterface: DialogInterface, i: Int ->
                        textWalletName?.visibility = View.VISIBLE
                        btnDisconnect?.visibility = View.VISIBLE
                        textAdd?.visibility = View.VISIBLE
                        textConnect?.visibility = View.VISIBLE
                        textWalletName?.text = peer.name
                        textUrl?.text = peer.url

                        val session:WCSession = WCSession.from(qrcode)!!
                        val uuid = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

                        val storeitem = WCSessionStoreItem(session,uuid,peerId,peer)
                        storage.session = storeitem

                        val credentials = Credentials.create(key)
                        var address = ""

                        if (qrcode.contains("binance")){
                            address = wallet.address
                            textAddress?.text = address
                        }
                        else{
                            address = credentials.address
                            textAddress?.text = address
                        }


                        try {
                            Picasso.with(this@WalletConnectActivity).load(peer.icons[0]).into(imgWallet)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        wcClient?.approveSession(
                            listOf(address),
                            Constants.WALLETCHAINID
                        )
                    }
                    setNegativeButton(getString(R.string.reject)) { dialogInterface: DialogInterface, i: Int ->
                        wcClient?.rejectRequest(id, "Rejected")
                        wcClient?.rejectSession()
                        wcClient?.killSession()
                        dialogInterface.dismiss()
                        finish()
                        Toast.makeText(
                            this@WalletConnectActivity,
                            getString(R.string.session_rejected),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.show()

            }

        }


    }

    override fun ethSign(id: Long, message: WCEthereumSignMessage) {
        if (!isDestroyed && !isFinishing){
            try {
                handler.post {
                    MaterialAlertDialogBuilder(this, R.style.MyAlertDialog).apply {
                        setMessage(getString(R.string.confirm_sign) + message.raw[0])
                        setPositiveButton(getString(R.string.accept)) { dialogInterface: DialogInterface, i: Int ->
                            wcClient?.signEth(id, message.raw[0])
                            dialogInterface.dismiss()
                        }
                        setNegativeButton(getString(R.string.cancel)) { dialogInterface: DialogInterface, i: Int ->

                            wcClient?.rejectRequest(id, "Rejected")
                            dialogInterface.dismiss()
                        }
                    }.show()

                }
            }
            catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    override fun ethSendTransaction(id: Long, transaction: WCEthereumTransaction) {

        if (!isDestroyed && !isFinishing){
            try {
                handler.post {
                    val value = Numeric.cleanHexPrefix(transaction.value).toLong(16)
                    val ether = Convert.fromWei(value.toString(), Convert.Unit.ETHER)
                    val send = getString(R.string.send)
                    val to = getString(R.string.to)


                    MaterialAlertDialogBuilder(this, R.style.MyAlertDialog).apply {
                        setMessage("$send $ether $to ${transaction.to}")
                        setPositiveButton(getString(R.string.approve)) { dialogInterface: DialogInterface, i: Int ->
                            dialogInterface.dismiss()
                            wcClient?.approveEthOrder(id, transaction)
                        }
                        setNegativeButton(getString(R.string.reject)) { dialogInterface: DialogInterface, i: Int ->
                            wcClient?.rejectRequest(id, "Rejected")
                            dialogInterface.dismiss()
                        }
                    }.show()

                }
            }
            catch (e:Exception){
                e.printStackTrace()
            }
        }




    }

    override fun bnbsign(id: Long, order: WCBinanceTransferOrder) {

        if (!isDestroyed && !isFinishing){
            try {
                handler.post {
                    val value = order.msgs[0].outputs[0].coins[0].amount
                    val toadd = order.msgs[0].outputs[0].address
                    val amount = value.toDouble()/100000000
                    val bnb = amount.toString()+" BNB"
                    val send = getString(R.string.send)
                    val to = getString(R.string.to)


                    MaterialAlertDialogBuilder(this, R.style.MyAlertDialog).apply {
                        setMessage("$send $bnb $to $toadd")
                        setPositiveButton(getString(R.string.approve)) { dialogInterface: DialogInterface, i: Int ->
                            val privatekey = PrivateKey(end,false)
                            val orderJson: String = GsonBuilder().serializeNulls().create().toJson(order)
                            val signature: ByteArray = Signature.signMessage(orderJson.toByteArray(), privatekey)
                            val signed = WCBinanceOrderSignature(
                                Hex.toHexString(signature),
                                Hex.toHexString(privatekey.publicKey.bytes)
                            )
                            wcClient?.approveBnbOrder(id, signed)

                            dialogInterface.dismiss()

                        }
                        setNegativeButton(getString(R.string.reject)) { dialogInterface: DialogInterface, i: Int ->
                            wcClient?.rejectRequest(id, "Rejected")
                            dialogInterface.dismiss()
                        }
                    }.show()

                }
            }
            catch (e:Exception){
                e.printStackTrace()
            }
        }


    }

    override fun bnbTradePair(id: Long, order: WCBinanceTradePair) {

        if (!isDestroyed && !isFinishing){
            try {
                handler.post {

                    MaterialAlertDialogBuilder(this, R.style.MyAlertDialog).apply {
                        setMessage("Trade Order Pair")
                        setPositiveButton(getString(R.string.approve)) { dialogInterface: DialogInterface, i: Int ->
                            val privatekey = PrivateKey(end,false)
                            val orderJson: String = GsonBuilder().serializeNulls().create().toJson(order)
                            val signature: ByteArray = Signature.signMessage(orderJson.toByteArray(), privatekey)
                            val signed = WCBinanceOrderSignature(
                                Hex.toHexString(signature),
                                Hex.toHexString(privatekey.publicKey.bytes)
                            )
                            Log.d("sign",Hex.toHexString(signature))
                            wcClient?.approveBnbOrder(id, signed)

                            dialogInterface.dismiss()

                        }
                        setNegativeButton(getString(R.string.reject)) { dialogInterface: DialogInterface, i: Int ->
                            wcClient?.rejectRequest(id, "Rejected")
                            dialogInterface.dismiss()
                        }
                    }.show()

                }
            }
            catch (e:Exception){
                e.printStackTrace()
            }
        }



    }

    override fun bnbTrade(id: Long, order: WCBinanceTradeOrder) {

        if (!isDestroyed && !isFinishing){
            try {
                handler.post {

                    MaterialAlertDialogBuilder(this, R.style.MyAlertDialog).apply {
                        val symbol = order.msgs[0].symbol
                        val myprice = order.msgs[0].price.toDouble()
                        val price = myprice/100000000
                        val sender = order.msgs[0].sender
                        val myquanty = order.msgs[0].quantity.toDouble()
                        val quantity = myquanty/100000000
                        var orderSide:OrderSide
                        val amount = price*quantity
                        if (order.msgs[0].side == 1){
                            orderSide = OrderSide.BUY
                        }
                        else {
                            orderSide = OrderSide.SELL
                        }
                        setTitle(getString(R.string.trade_order))
                        val trade = getString(R.string.trade)
                        val pricetext = getString(R.string.price)
                        setMessage("$trade ${UtilsDefault.formatCryptoCurrency(amount.toString())} $symbol($pricetext: ${UtilsDefault.formatCryptoCurrency(price.toString())})")
                        setPositiveButton(getString(R.string.approve)) { dialogInterface: DialogInterface, i: Int ->

                            wallet.sequence = order.sequence.toLong()
                            wallet.accountNumber = order.accountNumber.toInt()
                            wallet.chainId = order.chainId

                            val options = TransactionOption("",order.source.toLong(),null)
                            val createneworder = NewOrder()
                            createneworder.orderId = order.msgs[0].id
                            createneworder.orderType = OrderType.LIMIT
                            createneworder.side = orderSide
                            createneworder.price = price.toString()
                            createneworder.quantity = quantity.toString()
                            createneworder.symbol = symbol
                            createneworder.sender = sender
                            createneworder.timeInForce = TimeInForce.GTE

                            val bean:NewOrderMessage = createNewOrderMessage(createneworder)!!
                            val signature = signdata(bean,wallet,options)

                            val privatekey = PrivateKey(end,false)

                            val signed = WCBinanceOrderSignature(
                                Hex.toHexString(signature),
                                Hex.toHexString(privatekey.publicKey.bytes)
                            )
                            wcClient?.approveBnbOrder(id, signed)
                            dialogInterface.dismiss()

                        }
                        setNegativeButton(getString(R.string.reject)) { dialogInterface: DialogInterface, i: Int ->
                            wcClient?.rejectRequest(id, "Rejected")
                            dialogInterface.dismiss()
                        }
                    }.show()

                }
            }
            catch (e:Exception){
                e.printStackTrace()
            }
        }



    }

    override fun bnbCancel(id: Long, order: WCBinanceCancelOrder) {

        if (!isDestroyed && !isFinishing){
            try {
                handler.post {

                    MaterialAlertDialogBuilder(this, R.style.MyAlertDialog).apply {

                        val symbol = order.msgs[0].symbol
                        val orderId = order.msgs[0].refid
                        setTitle(getString(R.string.cancel_order))
                        val cancel = getString(R.string.cancel)
                        val ordertext = getString(R.string.order)
                        setMessage("$cancel $symbol $ordertext : $orderId")
                        setPositiveButton(getString(R.string.approve)) { dialogInterface: DialogInterface, i: Int ->
                            val privatekey = PrivateKey(end,false)
                            val orderJson: String = GsonBuilder().serializeNulls().create().toJson(order)
                            val signature: ByteArray = Signature.signMessage(orderJson.toByteArray(), privatekey)
                            val signed = WCBinanceOrderSignature(
                                Hex.toHexString(signature),
                                Hex.toHexString(privatekey.publicKey.bytes)
                            )
                            wcClient?.approveBnbOrder(id, signed)

                            dialogInterface.dismiss()

                        }
                        setNegativeButton(getString(R.string.reject)) { dialogInterface: DialogInterface, i: Int ->
                            wcClient?.rejectRequest(id, "Rejected")
                            dialogInterface.dismiss()
                        }
                    }.show()

                }
            }
            catch (e:Exception){
                e.printStackTrace()
            }
        }


    }

    override fun onBackPressed() {
        leaveSession()

    }

    @Throws(NoSuchAlgorithmException::class, IOException::class)
    fun signdata(msg: BinanceDexTransactionMessage, wallet: Wallet, options:TransactionOption): ByteArray? {
        val sd =
            SignData()
        sd.chainId = wallet.chainId
        sd.accountNumber = wallet.accountNumber.toString()
        sd.sequence = wallet.sequence.toString()
        sd.msgs = arrayOf(msg)
        sd.memo = options.memo
        sd.source = options.source.toString()
        sd.data = options.data
        return if (wallet.ecKey == null && wallet.ledgerKey != null) {
            Crypto.sign(
                EncodeUtils.toJsonEncodeBytes(sd),
                wallet.ledgerKey
            )
        } else Crypto.sign(
            EncodeUtils.toJsonEncodeBytes(sd),
            wallet.ecKey
        )
    }

    fun createNewOrderMessage(
        newOrder: NewOrder
    ): NewOrderMessage? {
        return NewOrderMessage.newBuilder()
            .setId(generateOrderId())
            .setOrderType(newOrder.orderType)
            .setPrice(newOrder.price)
            .setQuantity(newOrder.quantity)
            .setSender(wallet.address)
            .setSide(newOrder.side)
            .setSymbol(newOrder.symbol)
            .setTimeInForce(newOrder.timeInForce)
            .build()
    }

    private fun generateOrderId(): String? {
        return EncodeUtils.bytesToHex(wallet.addressBytes).toUpperCase() + "-" + (wallet.sequence + 1)
    }

    override fun onStart() {
        super.onStart()
        status = true
    }

    override fun onStop() {
        super.onStop()
        status = false
    }


    class doContractAsync(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            handler()
            return null
        }
    }
}
