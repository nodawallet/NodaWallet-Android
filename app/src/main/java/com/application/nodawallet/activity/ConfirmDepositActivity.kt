package com.application.nodawallet.activity

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.application.nodawallet.R
import com.application.nodawallet.application.NodaApplication
import com.application.nodawallet.model.*
import com.application.nodawallet.retrofit.NodaWalletAPI
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.UtilsDefault
import com.application.nodawallet.viewmodel.HistoryViewModel
import com.binance.dex.api.client.BinanceDexApiClientFactory
import com.binance.dex.api.client.BinanceDexEnvironment
import com.binance.dex.api.client.Wallet
import com.binance.dex.api.client.domain.broadcast.TransactionOption
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_confirm_deposit.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.LegacyAddress
import org.bitcoinj.core.Sha256Hash
import org.bitcoinj.crypto.*
import org.bitcoinj.params.AbstractBitcoinNetParams
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.wallet.DeterministicKeyChain
import org.bitcoinj.wallet.DeterministicSeed
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.contracts.eip20.generated.ERC20
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.http.HttpService
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.StaticGasProvider
import org.web3j.tx.response.NoOpProcessor
import org.web3j.utils.Convert
import org.web3j.utils.Numeric
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

class ConfirmDepositActivity : BaseActivity() {

    var credentials: Credentials? = null
    var web3: Web3j? = null
    var wallet: Wallet? = null
    var key = ""
    var receiveAddress = ""
    var depoAmount = ""
    var balance = ""
    var coinsymbol = ""
    var marketprice = ""
    var decimal = ""
    var isBalanceAvailabe = true
    var cointype = ""
    var pubaddress = ""
    var finalreceive = ""
    var netfee = 0.0
    var useraddress = ""
    var seedlist: ArrayList<String> = ArrayList()


    private lateinit var historyViewModel: HistoryViewModel
    var btcFees: BtcFees? = null


    @Inject
    lateinit var nodaWalletAPI: NodaWalletAPI


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_deposit)

        NodaApplication.instance.mComponent.inject(this)

        historyViewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)


        val intent = intent
        if (intent != null) {
            key = intent.getStringExtra("key") ?: ""
            receiveAddress = intent.getStringExtra("receiveAddress") ?: ""
            depoAmount = intent.getStringExtra("depoAmount") ?: ""
            balance = intent.getStringExtra("balance") ?: ""
            cointype = intent.getStringExtra("cointype") ?: ""
            pubaddress = intent.getStringExtra("address") ?: ""
            coinsymbol = intent.getStringExtra("coinsymbol") ?: ""
            decimal = intent.getStringExtra("decimal") ?: ""
            marketprice = intent.getStringExtra("marketprice") ?: ""
        }

        textToAdd?.text = receiveAddress

        imgBackSend.setOnClickListener {
            onBackPressed()
        }

        btnConfirm.setOnClickListener {
            if (isBalanceAvailabe) {
                if (coinsymbol == "BTC" || coinsymbol == "LTC") {
                    sendBtc(receiveAddress, finalreceive, coinsymbol)
                } else if (coinsymbol == "BNB" || cointype == "BEP2") {
                    sendBnB(receiveAddress, finalreceive)
                } else {
                    sendTransaction(receiveAddress, finalreceive)
                }
            }

        }

        initCredentials()

    }

    private fun sendBnB(receiveAddress: String, finalreceive: String) {
        doContractAsync {
            try {
                showProgress()
                val node = BinanceDexApiClientFactory.newInstance().newNodeRpcClient(
                    BinanceDexEnvironment.PROD.nodeUrl,
                    BinanceDexEnvironment.PROD.hrp
                )
                val transfer = com.binance.dex.api.client.domain.broadcast.Transfer()
                transfer.coin = coinsymbol
                transfer.amount = finalreceive
                transfer.toAddress = receiveAddress
                transfer.fromAddress = wallet!!.address
                val options = TransactionOption("nodawallet", 0, null)
                val response = node.transfer(transfer, wallet, options, false)

                if (response != null) {
                    if (response[0].isOk) {
                        val hash = response[0].hash
                        runOnUiThread {
                            hideProgress()
                            Toast.makeText(
                                this@ConfirmDepositActivity,
                                getString(R.string.withdraw_request),
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()
                        }
                    } else {
                        runOnUiThread {
                            hideProgress()

                            Toast.makeText(
                                this,
                                getString(R.string.withdraw_failed),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        finish()
                    }
                } else {
                    runOnUiThread {
                        hideProgress()
                        Toast.makeText(
                            this,
                            getString(R.string.withdraw_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    finish()
                }


            } catch (e: Exception) {
                runOnUiThread {
                    hideProgress()
                    Toast.makeText(this, getString(R.string.withdraw_failed), Toast.LENGTH_SHORT).show()
                }
                finish()
                e.printStackTrace()
            }
        }.execute()
    }

    private fun sendBtc(receiveAddress: String, finalreceive: String, coinsymbol: String) {

        showProgress()
        val params: AbstractBitcoinNetParams?
        val keypath: MutableList<ChildNumber>?
        var symbol = ""

        seedlist.clear()
        val seed = UtilsDefault.getSharedPreferenceString(Constants.SEED)
        val seedbytes = seed!!.split(" ")

        for (words in seedbytes) {
            seedlist.add(words)
        }

        val time = System.currentTimeMillis() / 1000
        val seede = DeterministicSeed(seed, null, "", time)
        val chain = DeterministicKeyChain.builder().seed(seede).build()

        if (coinsymbol == "BTC") {
            params = MainNetParams.get()
            keypath = HDUtils.parsePath("M/44H/0H/0H/0/0")
            symbol = "btc"
        } else {
            params = LitecoinMainNetParams.get()
            keypath = HDUtils.parsePath("M/44H/2H/0H/0/0")
            symbol = "ltc"
        }


        val key = chain.getKeyByPath(keypath, true)
        val privateKey = key.getPrivateKeyEncoded(params)
        val publicKey = key.publicKeyAsHex
        val address = LegacyAddress.fromKey(params, key).toBase58()
        useraddress = address
        val eckey = privateKey.key

        val value = finalreceive.toDouble()
        val vv = value * 100000000
        val myvalue = vv.toLong()

        val inputlist: ArrayList<BTCInput> = ArrayList()
        inputlist.add(BTCInput(arrayListOf(address)))

        val outputlist: ArrayList<BTCOutput> = ArrayList()
        outputlist.add(BTCOutput(arrayListOf(receiveAddress), myvalue))


        val sendbtc = BtcSendTransaction(inputlist, outputlist, 1, "medium")
        val payload = Gson().toJson(sendbtc)
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), payload)


        try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://api.blockcypher.com/v1/$symbol/main/txs/new")
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    hideProgress()
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    val resp = response.body?.string()
                    val topic = Gson().fromJson(resp, BTCSendResponse::class.java)
                    val sign = topic.tosign?.get(0)
                    if (sign != null) {
                        signTransaction(topic.tosign, topic.tx, eckey, publicKey, symbol)
                    } else {
                        hideProgress()
                        runOnUiThread {
                            hideProgress()
                            Toast.makeText(
                                this@ConfirmDepositActivity,
                                "Transaction failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

            })

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private fun signTransaction(
        sign: List<String>?,
        tx: Tx?,
        eckey: ECKey,
        publicKey: String,
        symbol: String
    ) {

        try {

            val tosign: ArrayList<String> = ArrayList()
            val signatures: ArrayList<String> = ArrayList()
            val pubkey: ArrayList<String> = ArrayList()


            tosign.clear()
            signatures.clear()
            pubkey.clear()

            for (i in 0..sign?.size!!.minus(1)) {

                val has = Sha256Hash.wrap(sign[i])
                val sing = eckey.sign(has)
                val res = sing.encodeToDER()
                val hex = Numeric.toHexStringNoPrefix(res)

                tosign.add(sign[i])
                signatures.add(hex)
                pubkey.add(publicKey)


            }

            val sendtrans = BTCSignTransaction(tx, tosign, signatures, pubkey)
            val payload = Gson().toJson(sendtrans)
            val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), payload)

            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://api.blockcypher.com/v1/$symbol/main/txs/send?token=")
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : okhttp3.Callback {
                    override fun onFailure(call: okhttp3.Call, e: IOException) {
                        hideProgress()

                    }

                    override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                        try {
                            val resp = response.body?.string()
                            val topic = Gson().fromJson(resp, BTCSendResponse::class.java)
                            runOnUiThread {
                                hideProgress()
                                Toast.makeText(
                                    this@ConfirmDepositActivity,
                                    getString(R.string.withdraw_request),
                                    Toast.LENGTH_SHORT
                                ).show()

                                finish()
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                })

            } catch (e: Exception) {
                hideProgress()
                e.printStackTrace()
            }

        } catch (e: Exception) {
            hideProgress()
            e.printStackTrace()
        }

    }


    @SuppressLint("SetTextI18n")
    private fun initCredentials() {

        if (coinsymbol == "BTC" || coinsymbol == "LTC") {

            seedlist.clear()
            val seed = UtilsDefault.getSharedPreferenceString(Constants.SEED)
            val seedbytes = seed!!.split(" ")

            for (words in seedbytes) {
                seedlist.add(words)
            }

            val params: AbstractBitcoinNetParams?
            val keypath: MutableList<ChildNumber>?
            var symbol = ""
            val time = System.currentTimeMillis() / 1000

            if (coinsymbol == "BTC") {
                params = MainNetParams.get()
                keypath = HDUtils.parsePath("M/44H/0H/0H/0/0")
                symbol = "btc"
            } else {
                params = LitecoinMainNetParams.get()
                keypath = HDUtils.parsePath("M/44H/2H/0H/0/0")
                symbol = "ltc"
            }

            val seede = DeterministicSeed(seed, null, "", time)

            val chain = DeterministicKeyChain.builder().seed(seede).build()
            val key = chain.getKeyByPath(keypath, true)
            val privateKey = key.getPrivateKeyEncoded(params)
            Log.d("wif", privateKey.toBase58())
            val address = LegacyAddress.fromKey(params, key).toBase58()
            textFromAdd?.text = address
            getBTCFees(symbol)

        } else if (coinsymbol == "BNB" || cointype == "BEP2") {

            wallet = Wallet(key, BinanceDexEnvironment.PROD)
            textFromAdd?.text = wallet!!.address
            getBnbFees()

        } else if (cointype == "Token" || coinsymbol == "ETH") {
            web3 = Web3j.build(HttpService(Constants.INFURA))
            credentials = Credentials.create(key)
            textFromAdd?.text = credentials!!.address
            getTransFee()
        }

    }

    private fun getBnbFees() {
        var bnbfees = 0.0
        showProgress()
        doContractAsync {
            val node = BinanceDexApiClientFactory.newInstance().newNodeRpcClient(
                BinanceDexEnvironment.PROD.nodeUrl,
                BinanceDexEnvironment.PROD.hrp
            )
            val fee = node.fees
            for (i in 0..fee.size.minus(1)) {

                if (fee[i].fixedFeeParams != null) {
                    hideProgress()

                    if (fee[i].fixedFeeParams.msgType == "send") {
                        bnbfees = fee[i].fixedFeeParams.fee.toDouble() / 100000000
                        runOnUiThread {
                            val currency = UtilsDefault.getSharedPreferenceString(Constants.CURRENCY)
                            val symbol = currency?.split("-")!![1]

                            var receive = depoAmount.toDouble() + bnbfees
                            val amount = depoAmount.toDouble()
                            val market = marketprice.toDouble()
                            val final = amount * market
                            if (receive >= balance.toDouble()) {
                                finalreceive = (balance.toDouble() - bnbfees).toString()
                                textSendAmount?.text =
                                    "-" + UtilsDefault.formatCryptoCurrency(finalreceive) + " " + coinsymbol + " (" + symbol + UtilsDefault.formatDecimal(
                                        final.toString()
                                    ) + ")"
                            } else {
                                textSendAmount?.text =
                                    "-" + UtilsDefault.formatCryptoCurrency(depoAmount) + " " + coinsymbol + " (" + symbol + UtilsDefault.formatDecimal(
                                        final.toString()
                                    ) + ")"
                                finalreceive = depoAmount
                            }


                            textFee?.text = UtilsDefault.formatCryptoCurrency(bnbfees.toString())
                            textReceiveBalance?.text =
                                UtilsDefault.formatCryptoCurrency(receive.toString())

                            if (balance.toDouble() < bnbfees) {
                                btnConfirm.text = getString(R.string.insufficient_bal)
                                btnConfirm.alpha = 0.5F
                                isBalanceAvailabe = false

                            } else {
                                btnConfirm.text = getString(R.string.confirm)
                                btnConfirm.alpha = 1F
                                isBalanceAvailabe = true

                            }
                        }

                    }

                }

            }

        }.execute()

    }

    private fun getBTCFees(symbol: String) {

        if (UtilsDefault.isOnline()) {
            showProgress()
            nodaWalletAPI.getbtcfees("https://api.blockcypher.com/v1/$symbol/main")
                .enqueue(object : Callback<BtcFees> {
                    override fun onFailure(call: Call<BtcFees>, t: Throwable) {
                        hideProgress()
                    }

                    override fun onResponse(call: Call<BtcFees>, response: Response<BtcFees>) {

                        btcFees = response.body()
                        hideProgress()

                        if (btcFees != null) {

                            val transfee = btcFees?.medium_fee_per_kb!!
                            val fee = transfee / 100000000

                            val currency =
                                UtilsDefault.getSharedPreferenceString(Constants.CURRENCY)
                            val symbol = currency?.split("-")!![1]

                            var receive = depoAmount.toDouble() + fee
                            val amount = depoAmount.toDouble()
                            val market = marketprice.toDouble()
                            val final = amount * market
                            if (receive >= balance.toDouble()) {
                                finalreceive = (balance.toDouble() - fee).toString()
                                textSendAmount?.text =
                                    "-" + UtilsDefault.formatCryptoCurrency(finalreceive) + " " + coinsymbol + " (" + symbol + UtilsDefault.formatDecimal(
                                        final.toString()
                                    ) + ")"
                            } else {
                                textSendAmount?.text =
                                    "-" + UtilsDefault.formatCryptoCurrency(depoAmount) + " " + coinsymbol + " (" + symbol + UtilsDefault.formatDecimal(
                                        final.toString()
                                    ) + ")"
                                finalreceive = depoAmount
                            }



                            textFee?.text = UtilsDefault.formatCryptoCurrency(fee.toString())
                            textReceiveBalance?.text =
                                UtilsDefault.formatCryptoCurrency(receive.toString())

                            if (balance.toDouble() < fee) {
                                btnConfirm.text = getString(R.string.insufficient_bal)
                                btnConfirm.alpha = 0.5F
                                isBalanceAvailabe = false

                            } else {
                                btnConfirm.text = getString(R.string.confirm)
                                btnConfirm.alpha = 1F
                                isBalanceAvailabe = true

                            }

                        }


                    }

                })

        }

    }

    private fun getTransFee() {

        showProgress()
        doContractAsync {


            val encodedFunction = FunctionEncoder.encode(depositCall())
            val gasprice = web3?.ethGasPrice()?.send()!!.gasPrice
            val gaslimitVal = web3?.ethEstimateGas(
                Transaction.createEthCallTransaction(
                    credentials?.address,
                    receiveAddress,
                    encodedFunction
                )
            )!!.send().amountUsed


            val gasused = Convert.fromWei(gasprice.toString(), Convert.Unit.GWEI)
            val fees = gaslimitVal * gasused.toBigInteger()

            val transfee = Convert.fromWei(fees.toString(), Convert.Unit.GWEI)
            netfee = transfee.toDouble()
            val currency = UtilsDefault.getSharedPreferenceString(Constants.CURRENCY)
            val symbol = currency?.split("-")!![1]

            val amount = depoAmount.toDouble()
            val market = marketprice.toDouble()
            val final = amount * market
            var receive = ""
            receive = (depoAmount.toBigDecimal() + transfee).toString()


            runOnUiThread {
                hideProgress()
                if (receive.toDouble() >= balance.toDouble()) {
                    finalreceive = (depoAmount.toBigDecimal() - transfee).toString()
                    textSendAmount?.text =
                        "-" + finalreceive + " " + coinsymbol + " (" + symbol + UtilsDefault.formatDecimal(
                            final.toString()
                        ) + ")"
                    receive = depoAmount
                } else {
                    textSendAmount?.text =
                        "-" + depoAmount + " " + coinsymbol + " (" + symbol + UtilsDefault.formatDecimal(
                            final.toString()
                        ) + ")"
                    finalreceive = depoAmount
                    receive = (depoAmount.toBigDecimal() + transfee).toString()
                }


                textFee?.text = transfee.toString()
                textReceiveBalance?.text = receive

                if (balance.toDouble() < netfee) {
                    btnConfirm.text = getString(R.string.insufficient_bal)
                    btnConfirm.alpha = 0.5F
                    isBalanceAvailabe = false

                } else {
                    btnConfirm.text = getString(R.string.confirm)
                    btnConfirm.alpha = 1F
                    isBalanceAvailabe = true

                }
            }

        }.execute()

    }

    private fun sendTransaction(receiveAddress: String, depoAmount: String) {

        if (UtilsDefault.isOnline()) {
            showProgress()

            if (credentials != null) {
                doContractAsync {
                    if (getClientVersion()) {

                        if (cointype == "token" || cointype == "apitoken") {
                            TokenToEthTransaction(receiveAddress, depoAmount)
                        } else {
                            EthToEthTransaction(receiveAddress, depoAmount.toDouble())
                        }
                    }

                }.execute()
            }

        } else {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }

    }


    private fun TokenToEthTransaction(receiveAddress: String, depoAmount: String) {

        val tokencontractAddress = pubaddress
        var decimalpoint = decimal.toDouble().toInt()

        val nonce =
            web3!!.ethGetTransactionCount(credentials!!.address, DefaultBlockParameterName.LATEST)
                .send().transactionCount

        val gasPrice = web3?.ethGasPrice()?.send()!!.gasPrice
        val gasLimit = web3?.ethEstimateGas(
            Transaction.createContractTransaction(
                credentials?.address,
                nonce,
                gasPrice,
                ""
            )
        )!!.send().amountUsed


        val gasProvider = StaticGasProvider(gasPrice, gasLimit)

        val transactionReceiptProcessor = NoOpProcessor(web3)
        val transactionManager = RawTransactionManager(
            web3!!,
            credentials!!,
            Constants.CHAINID,
            transactionReceiptProcessor
        )


        val erc = ERC20.load(tokencontractAddress, web3, transactionManager, gasProvider)
        try {
            var depo = depoAmount
            val tkndecimal = BigDecimal.TEN.pow(decimalpoint)
            val value = tkndecimal * depo.toBigDecimal()

            val tx = erc?.transfer(receiveAddress, value.toBigInteger())
            val result = tx?.send()

            if (result?.transactionHash != null) {
                runOnUiThread {
                    hideProgress()

                    Toast.makeText(this, getString(R.string.withdraw_request), Toast.LENGTH_SHORT)
                        .show()
                    val deposit = depoAmount
                    val transhash = result.transactionHash.toString()

                }
                finish()

            } else {
                runOnUiThread {
                    hideProgress()
                    Toast.makeText(this, getString(R.string.withdraw_failed), Toast.LENGTH_SHORT)
                        .show()
                }
                finish()
            }

        } catch (e: Exception) {
            runOnUiThread {
                hideProgress()
                Toast.makeText(this, getString(R.string.insufficientgas), Toast.LENGTH_SHORT).show()

            }
            finish()
            e.printStackTrace()
        }
    }

    private fun EthToEthTransaction(receiveAddress: String, depoAmount: Double) {

        try {

            val value = Convert.toWei(depoAmount.toString(), Convert.Unit.ETHER).toBigInteger()
            val nonce = web3!!.ethGetTransactionCount(
                credentials!!.address,
                DefaultBlockParameterName.LATEST
            ).send().transactionCount

            val transactionReceiptProcessor = NoOpProcessor(web3)
            val transactionManager = RawTransactionManager(
                web3!!,
                credentials!!,
                Constants.CHAINID,
                transactionReceiptProcessor
            )

            val encodedFunction = FunctionEncoder.encode(depositCall())

            val gasPrice = web3?.ethGasPrice()?.send()!!.gasPrice
            val gasLimit = web3?.ethEstimateGas(
                Transaction.createEthCallTransaction(
                    credentials?.address,
                    receiveAddress,
                    encodedFunction
                )
            )!!.send().amountUsed

            val rawTransaction = RawTransaction.createTransaction(
                nonce,
                gasPrice,
                gasLimit,
                receiveAddress,
                value,
                encodedFunction
            )

            val sendTransaction = transactionManager.signAndSend(rawTransaction)

            if (sendTransaction.transactionHash != null) {
                runOnUiThread {
                    hideProgress()
                    Toast.makeText(this, getString(R.string.withdraw_request), Toast.LENGTH_LONG)
                        .show()
                    val deposit = depoAmount.toString().trim()
                    val transhash = sendTransaction.transactionHash.toString()

                }
                finish()
            } else {
                val err = sendTransaction.error.message
                runOnUiThread {
                    val send = depoAmount.toString().trim()
                    hideProgress()
                    Toast.makeText(this, "$send $coinsymbol " + err, Toast.LENGTH_SHORT).show()

                }
                finish()
            }


        } catch (e: Exception) {
            runOnUiThread {
                hideProgress()
                Toast.makeText(this, getString(R.string.insufficientgas), Toast.LENGTH_SHORT).show()
            }
            finish()
            e.printStackTrace()
        }

    }



    private fun getClientVersion(): Boolean {
        try {
            Log.d("aaa==", web3.toString())
            val web3ClientVersion = web3!!.web3ClientVersion().send()
            val clientVersion = web3ClientVersion.web3ClientVersion
            Log.d("WEB3", "clientVersion " + clientVersion)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false


    }

    fun depositCall(): Function {
        val function = Function(
            "deposit",
            Arrays.asList(),
            Arrays.asList<TypeReference<*>>(object : TypeReference<Uint256>() {
            })
        )
        return function
    }


    class doContractAsync(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            handler()
            return null
        }
    }

}
