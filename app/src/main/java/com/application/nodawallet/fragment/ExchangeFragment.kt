package com.application.nodawallet.fragment

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.nodawallet.R
import com.application.nodawallet.activity.ConfirmDepositActivity
import com.application.nodawallet.activity.MainActivity.Companion.mainBack
import com.application.nodawallet.adapter.ExchangeCoinAdapter
import com.application.nodawallet.application.NodaApplication
import com.application.nodawallet.model.*
import com.application.nodawallet.retrofit.NodaWalletAPI
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.UtilsDefault
import com.application.nodawallet.viewmodel.HistoryViewModel
import com.application.nodawallet.viewmodel.MultiCoinViewModel
import com.binance.dex.api.client.BinanceDexApiClientFactory
import com.binance.dex.api.client.BinanceDexEnvironment
import com.binance.dex.api.client.Wallet
import com.binance.dex.api.client.domain.broadcast.TransactionOption
import com.binance.dex.api.client.domain.broadcast.Transfer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_exchange.*
import kotlinx.android.synthetic.main.fragment_token.*
import kotlinx.android.synthetic.main.layout_header.*
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
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.wallet.DeterministicKeyChain
import org.bitcoinj.wallet.DeterministicSeed
import org.json.JSONArray
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.*
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.generated.Bytes32
import org.web3j.abi.datatypes.generated.Uint128
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.abi.datatypes.generated.Uint8
import org.web3j.contracts.eip20.generated.ERC20
import org.web3j.crypto.*
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.http.HttpService
import org.web3j.tx.ChainIdLong
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
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class ExchangeFragment : BaseFragment(R.layout.fragment_exchange) {

    var credentials: Credentials? = null
    var web3: Web3j? = null
    var wallet:Wallet? = null
    var phrase = ""
    var walletId = 0
    var toValue = ""

    var quantity = ""
    var price = 0.0
    var priceamout: BigDecimal? = null
    var reqfromAddress = ""
    var reqtoAddress = ""
    var sendtype = ""
    var receivetype = ""
    var marketResponse: MarketResponse? = null

    var fromMarket = ""
    var toMarket = ""
    var calcfees = "0.0001"
    var myfees = "0.0"
    var adminBalance = 0.0

    private lateinit var viewCoinmodel: MultiCoinViewModel
    val multicoinlist = ArrayList<CoinList>()

    var fromlist = ArrayList<CoinList>()
    var tolist = ArrayList<CoinList>()
    var fromtemp = ArrayList<CoinList>()
    var totemp = ArrayList<CoinList>()
    var v = ""
    var r = ""
    var s = ""
    var message = ""
    var minvalue = ""
    var vrsUpdate = false
    var btcExchangeResponse: BTCExchangeResponse? = null
    var seedlist: ArrayList<String> = java.util.ArrayList()
    var templist:ArrayList<CoinList> =  ArrayList()



    @Inject
    lateinit var nodaWalletAPI: NodaWalletAPI

    private lateinit var historyViewModel: HistoryViewModel
    var vrsResponse: VrsResponse? = null
    var minimumBtcResponse: MinimumBtcResponse? = null
    var contractResponse: ContractResponse? = null
    private var exchangeCoinAdapter: ExchangeCoinAdapter? = null
    var fromdecimal = 0
    var todecimal = 0
    var fromCoin = ""
    var toCoin = ""
    var isSend = false
    var isReceive = false
    var mnemonic = ""
    var feehistory = ""


    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NodaApplication.instance.mComponent.inject(this)

        searchCoin.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val txt = s.toString()
                filterlist(txt)

            }

        })

        historyViewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)

        btnFurther.setOnClickListener {


            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                return@setOnClickListener
            }


            val cointype2 = UtilsDefault.getSharedPreferenceString(Constants.COINTYPE)
            if (cointype2 == "public") {
                Toast.makeText(
                    activity,
                    getString(R.string.transaction_publicaddress),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }


            val send = edtSend.text.toString().trim().replace(",", ".")
            val receive = edtReceive.text.toString().replace(",", ".")
            val min = textMin.text.toString().trim().replace(",", ".")
            val max = textMax.text.toString().trim().replace(",", ".")

            try {

                if (send != "") {
                    val sendvalue = send.toDouble()
                    if (sendvalue == 0.0) {
                        edtSend.error = getString(R.string.enter_amount_greater0)
                    } else {
                        quantity = receive
                        if (UtilsDefault.isOnline()) {
                            val sendbal =
                                textSendBalance.text.toString().trim().replace(",", ".").toDouble()
                            if (sendbal == 0.0) {
                                Toast.makeText(
                                    activity,
                                    getString(R.string.insufficient_balance),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (sendbal < send.toDouble()) {
                                Toast.makeText(
                                    activity,
                                    getString(R.string.insufficient_balance),
                                    Toast.LENGTH_SHORT
                                ).show()

                            } else if (send.toDouble() < min.toDouble() || send.toDouble() > max.toDouble()) {
                                Toast.makeText(
                                    activity,
                                    getString(R.string.amount_match_min_max),
                                    Toast.LENGTH_SHORT
                                ).show()

                            } else {
                                exchangeBtc(send)
                            }

                        }

                    }
                } else {
                    Toast.makeText(activity, getString(R.string.enter_amount_to_exchange), Toast.LENGTH_SHORT).show()

                }

            } catch (e: Exception) {
                e.printStackTrace()
            }


        }

        bottomSheetBehavior = BottomSheetBehavior.from<FrameLayout>(bottomsheetStockExchange)

        recycleExchangeCoin.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        exchangeCoinAdapter = ExchangeCoinAdapter(activity!!)
        recycleExchangeCoin!!.adapter = exchangeCoinAdapter

        viewCoinmodel = ViewModelProvider(this).get(MultiCoinViewModel::class.java)
        try {
            viewCoinmodel.getDataById.removeObservers(activity!!)
            viewCoinmodel.getDataById.observe(activity!!,
                Observer<List<MultiCoinList>> { coinlist ->

                    try {
                        multicoinlist.clear()
                        fromlist.clear()
                        tolist.clear()

                        for (i in 0..coinlist[0].list.size.minus(1)) {
                            if (coinlist[0].list[i].coinType != "token" && coinlist[0].list[i].coinType != "BEP2") {
                                if (coinlist[0].list[i].enableStatus == 1){
                                    Log.d("cointype",coinlist[0].list[i].coinType)
                                    multicoinlist.add(
                                        CoinList(
                                            coinlist[0].list[i].coinName,
                                            coinlist[0].list[i].coinImage,
                                            coinlist[0].list[i].tokenImage,
                                            coinlist[0].list[i].coinSymbol,
                                            coinlist[0].list[i].phrase,
                                            coinlist[0].list[i].coinType,
                                            coinlist[0].list[i].publicAddress,
                                            coinlist[0].list[i].privateKey,
                                            coinlist[0].list[i].publicKey,
                                            coinlist[0].list[i].enableStatus,
                                            coinlist[0].list[i].balance,
                                            coinlist[0].list[i].marketPrice,
                                            coinlist[0].list[i].marketPercentage,
                                            coinlist[0].list[i].decimal,
                                            coinlist[0].list[i].network,
                                            coinlist[0].list[i].minex,
                                            coinlist[0].list[i].maxex,
                                            coinlist[0].list[i].fees
                                        )
                                    )
                                }

                            }
                        }
                        fromlist.addAll(multicoinlist)
                        tolist.addAll(multicoinlist)
                        walletId = coinlist[0].id

                        if (multicoinlist.isNotEmpty()) {

                            fromCoin = multicoinlist[0].coinSymbol
                            toCoin = multicoinlist[1].coinSymbol

                            textSendCurrency?.text = fromCoin
                            textReceiveCurrency?.text = toCoin

                            UpdateValues()
                            exchangeCoinAdapter?.setCoins(multicoinlist)
                            initCredentials()

                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                })

        } catch (e: Exception) {
            e.printStackTrace()
        }

        llExSendCoin.setOnClickListener {

            fromtemp.clear()

            for (i in 0..fromlist.size.minus(1)) {
                if (fromCoin != fromlist[i].coinSymbol) {
                    fromtemp.add(
                        CoinList(
                            fromlist[i].coinName,
                            fromlist[i].coinImage,
                            fromlist[i].tokenImage,
                            fromlist[i].coinSymbol,
                            fromlist[i].phrase,
                            fromlist[i].coinType,
                            fromlist[i].publicAddress,
                            fromlist[i].privateKey,
                            fromlist[i].publicKey,
                            fromlist[i].enableStatus,
                            fromlist[i].balance,
                            fromlist[i].marketPrice,
                            fromlist[i].marketPercentage,
                            fromlist[i].decimal,
                            fromlist[i].network,
                            fromlist[i].minex,
                            fromlist[i].maxex,
                            fromlist[i].fees
                        )
                    )
                }
            }

            exchangeCoinAdapter?.setCoins(fromtemp)
            isSend = true
            isReceive = false

            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        textHeader.text = resources.getString(R.string.exchange)

        llExReceiveCoin.setOnClickListener {

            totemp.clear()

            for (i in 0..tolist.size.minus(1)) {
                if (toCoin != tolist[i].coinSymbol) {
                    totemp.add(
                        CoinList(
                            tolist[i].coinName,
                            tolist[i].coinImage,
                            tolist[i].tokenImage,
                            tolist[i].coinSymbol,
                            tolist[i].phrase,
                            tolist[i].coinType,
                            tolist[i].publicAddress,
                            tolist[i].privateKey,
                            tolist[i].publicKey,
                            tolist[i].enableStatus,
                            tolist[i].balance,
                            tolist[i].marketPrice,
                            tolist[i].marketPercentage,
                            tolist[i].decimal,
                            tolist[i].network,
                            tolist[i].minex,
                            tolist[i].maxex,
                            tolist[i].fees
                        )
                    )
                }
            }

            exchangeCoinAdapter?.setCoins(totemp)

            isReceive = true
            isSend = false

            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        exchangeCoinAdapter!!.onItemClick = { pos, view ->
            try {

                if (isSend) {

                    var search = searchCoin.text.toString().trim()
                    var model:CoinList? = null

                    model = if (search.isNotEmpty()){
                        templist[pos]

                    } else {
                        fromtemp[pos]
                    }


                    if (model.coinSymbol == toCoin) {
                        fromCoin = model.coinSymbol
                        toCoin = totemp[0].coinSymbol
                    } else {
                        fromCoin = model.coinSymbol
                    }

                    textSendCurrency?.text = fromCoin
                    textReceiveCurrency.text = toCoin
                    edtSend?.setText("")
                    edtReceive?.setText("")
                    searchCoin?.setText("")


                    UpdateValues()

                } else if (isReceive) {

                    var search = searchCoin.text.toString().trim()
                    var model:CoinList? = null

                    model = if (search.isNotEmpty()){
                        templist[pos]

                    } else {
                        totemp[pos]
                    }

                    if (model.coinSymbol == fromCoin) {
                        toCoin = model.coinSymbol
                        fromCoin = fromtemp[0].coinSymbol

                    } else {
                        toCoin = model.coinSymbol

                    }

                    textSendCurrency?.text = fromCoin
                    textReceiveCurrency.text = toCoin
                    edtSend?.setText("")
                    edtReceive?.setText("")
                    searchCoin?.setText("")

                    UpdateValues()


                }

                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        edtSend.addTextChangedListener(generalTextWatcher)
        edtReceive.addTextChangedListener(generalTextWatcher)


    }

    private fun filterlist(txt: String) {
        templist.clear()

        if (txt !=""){
            var searchtext = txt.toLowerCase()

            for (items in multicoinlist){
                val coinsy = items.coinSymbol.toLowerCase()
                if (coinsy.startsWith(searchtext)){
                    templist.add(items)
                }
            }
            exchangeCoinAdapter?.setCoins(templist)
        }
        else {
            exchangeCoinAdapter?.setCoins(multicoinlist)
        }


    }


    private fun exchangeBtc(sendAmount: String) {

        showProgress()

        val inputParams = InputParams()
        inputParams.from_currency = fromCoin
        inputParams.to_currency = toCoin
        inputParams.amount = sendAmount
        inputParams.receive_address = reqtoAddress
        inputParams.device = "Android"
        inputParams.firebase_id = UtilsDefault.getSharedPreferenceValuefcm(Constants.FCM_KEY)

        nodaWalletAPI.exchangebtc(inputParams).enqueue(object : Callback<BTCExchangeResponse> {
            override fun onFailure(call: Call<BTCExchangeResponse>, t: Throwable) {
                hideProgress()
            }

            override fun onResponse(
                call: Call<BTCExchangeResponse>,
                response: Response<BTCExchangeResponse>
            ) {

                btcExchangeResponse = response.body()
                if (btcExchangeResponse != null) {
                    if (btcExchangeResponse?.status == 1){
                        val address = btcExchangeResponse?.data?.deposit_address
                        val depoamount = btcExchangeResponse?.data?.deposit_amount
                        if (fromCoin == "BTC" || fromCoin == "LTC") {
                            sendBTC(address, depoamount!!.toDouble(),fromCoin)
                        }
                        else if (fromCoin == "BNB"){
                            sendBnB(address,depoamount,btcExchangeResponse?.data?.extra_id)
                        }
                        else {
                            EthAsync(address, depoamount!!.toDouble())
                        }
                    }
                    else {
                        hideProgress()
                    }

                }

            }

        })

    }

    private fun sendBnB(address: String?, depoamount: String?,memo:String?) {
        doContractAsync {
            try {
                showProgress()
                val node = BinanceDexApiClientFactory.newInstance().newNodeRpcClient(
                    BinanceDexEnvironment.PROD.nodeUrl,
                    BinanceDexEnvironment.PROD.hrp
                )
                val transfer = Transfer()
                transfer.coin = "BNB"
                transfer.amount = depoamount
                transfer.toAddress = address
                transfer.fromAddress = wallet!!.address
                val options = TransactionOption(memo, 0, null)
                val response = node.transfer(transfer, wallet, options, false)

                if (response != null) {
                    if (response[0].isOk) {
                        val hash = response[0].hash
                        Log.d("hash",hash)
                        activity?.runOnUiThread {
                            hideProgress()
                            Toast.makeText(activity, getString(R.string.withdraw_request), Toast.LENGTH_SHORT).show()

                        }
                    } else {
                        activity?.runOnUiThread {
                            hideProgress()
                            Toast.makeText(
                                activity,
                                getString(R.string.withdraw_failed),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    activity?.runOnUiThread {
                        hideProgress()
                        Toast.makeText(
                            activity,
                            getString(R.string.withdraw_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }


            } catch (e: Exception) {
                activity?.runOnUiThread {
                    hideProgress()
                    Toast.makeText(activity, getString(R.string.withdraw_failed), Toast.LENGTH_SHORT)
                        .show()
                }
                e.printStackTrace()
            }
        }.execute()
    }

    private fun EthAsync(address: String?, depoamount: Double?) {

        if (credentials != null) {
            doContractAsync {
                if (fromCoin == "ETH"){
                    sendEth(address, depoamount)
                }
                else {
                    sendToken(address,depoamount)
                }
            }.execute()
        }


    }

    private fun sendToken(address: String?, depoamount: Double?) {

        val reqAddress = reqfromAddress
        val decimalpoint = fromdecimal.toDouble().toInt()

        val nonce = web3!!.ethGetTransactionCount(credentials!!.address, DefaultBlockParameterName.LATEST).send().transactionCount

        val gasPrice = web3?.ethGasPrice()?.send()!!.gasPrice
        val gasLimit = web3?.ethEstimateGas(Transaction.createContractTransaction(credentials?.address,nonce,gasPrice,""))!!.send().amountUsed


        /* val gasPrice = BigInteger("40000000000")
         val gasLimit = BigInteger("210000")*/


        val gasProvider= StaticGasProvider(gasPrice,gasLimit)


        val transactionReceiptProcessor = NoOpProcessor(web3)
        val transactionManager = RawTransactionManager(
            web3!!,
            credentials!!,
            Constants.CHAINID,
            transactionReceiptProcessor
        )


        val erc= ERC20.load(reqAddress,web3,transactionManager,gasProvider)
        try {
            val depo = depoamount
            val tkndecimal = BigDecimal.TEN.pow(decimalpoint)
            val value = tkndecimal*depo!!.toBigDecimal()

            val tx = erc?.transfer(address,value.toBigInteger())
            val result = tx?.send()

            if(result?.transactionHash!=null){
                activity?.runOnUiThread {
                    hideProgress()
                    Toast.makeText(activity, getString(R.string.exchange_processed), Toast.LENGTH_LONG).show()

                }

            }
            else {
                activity?.runOnUiThread {
                    hideProgress()

                    Toast.makeText(
                        activity,getString(R.string.exchange_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
        catch (e:Exception){
            activity?.runOnUiThread {
                hideProgress()
                Toast.makeText(activity, getString(R.string.insufficientgas), Toast.LENGTH_SHORT).show()

            }
            e.printStackTrace()
        }
    }

    private fun sendEth(receiveAddress: String?, depoamount: Double?) {

        try {
            val value = Convert.toWei(depoamount.toString(), Convert.Unit.ETHER).toBigInteger()
            val nonce = web3!!.ethGetTransactionCount(credentials!!.address, DefaultBlockParameterName.LATEST).send().transactionCount


            val transactionReceiptProcessor = NoOpProcessor(web3)
            val transactionManager = RawTransactionManager(
                web3!!,
                credentials!!,
                Constants.CHAINID,
                transactionReceiptProcessor
            )

            val encodedFunction = FunctionEncoder.encode(depositCall())

            val gasPrice = web3?.ethGasPrice()?.send()!!.gasPrice
            val gasLimit = web3?.ethEstimateGas(Transaction.createEthCallTransaction(credentials?.address,receiveAddress,encodedFunction))!!.send().amountUsed


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
                activity?.runOnUiThread {
                    hideProgress()
                    Toast.makeText(activity, getString(R.string.exchange_processed), Toast.LENGTH_LONG).show()
                }

            } else {
                val err = sendTransaction.error.message
                Log.d("err",err)
                activity?.runOnUiThread {
                    hideProgress()
                    Toast.makeText(activity, ""+err, Toast.LENGTH_SHORT).show()
                }

            }
        }
        catch (e:Exception){
            e.printStackTrace()
        }



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


    private fun sendBTC(addressto: String?, depoamount: Double?,coinsymbol:String) {

        val params: AbstractBitcoinNetParams?
        val keypath:MutableList<ChildNumber>?
        var symbol = ""

        seedlist.clear()
        val seed = UtilsDefault.getSharedPreferenceString(Constants.SEED)
        val seedbytes = seed!!.split(" ")

        for (words in seedbytes){
            seedlist.add(words)
        }

        val time = System.currentTimeMillis()/1000
        val seede = DeterministicSeed(seed,null,"",time)
        val chain = DeterministicKeyChain.builder().seed(seede).build()


        if (coinsymbol == "BTC"){
            params = MainNetParams.get()
            keypath = HDUtils.parsePath("M/44H/0H/0H/0/0")
            symbol = "btc"
        }
        else {
            params = LitecoinMainNetParams.get()
            keypath = HDUtils.parsePath("M/44H/2H/0H/0/0")
            symbol = "ltc"
        }

        val key = chain.getKeyByPath(keypath,true)
        val privateKey =  key.getPrivateKeyEncoded(params)
        Log.d("wif",privateKey.toBase58())
        val publicKey  = key.publicKeyAsHex
        val address = LegacyAddress.fromKey(params,key).toBase58()
        val eckey  =  privateKey.key



        var inputlist: java.util.ArrayList<BTCInput> = java.util.ArrayList()
        inputlist.add(BTCInput(arrayListOf(address)))

            val depo = depoamount!! *100000000


        var outputlist: java.util.ArrayList<BTCOutput> = java.util.ArrayList()
        outputlist.add(BTCOutput(arrayListOf(addressto!!),depo.toLong()))


        var sendbtc = BtcSendTransaction(inputlist,outputlist,1,"medium")


        val payload = Gson().toJson(sendbtc)

        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(),payload)


        try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://api.blockcypher.com/v1/$symbol/main/txs/new")
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {

                    Log.d("failed","failure")

                    activity?.runOnUiThread {
                        hideProgress()
                        Toast.makeText(activity,"Transaction failed",Toast.LENGTH_SHORT).show() }
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    val resp = response.body?.string()
                    val topic = Gson().fromJson(resp, BTCSendResponse::class.java)
                    val sign = topic.tosign?.get(0)
                    if (sign!=null){
                        signTransaction(topic.tosign,topic.tx,eckey,publicKey,symbol)
                    }
                    else {
                        hideProgress()
                        activity?.runOnUiThread {
                            hideProgress()
                            Toast.makeText(activity,"Transaction failed",Toast.LENGTH_SHORT).show() }
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
        symbol:String
    ) {

        try {

            val tosign: java.util.ArrayList<String> = java.util.ArrayList()
            val signatures: java.util.ArrayList<String> = java.util.ArrayList()
            val pubkey: java.util.ArrayList<String> = java.util.ArrayList()


            tosign.clear()
            signatures.clear()
            pubkey.clear()

            for (i in 0..sign?.size!!.minus(1)){

                val has =  Sha256Hash.wrap(sign[i])
                val sing = eckey.sign(has)
                val res = sing.encodeToDER()
                val hex = Numeric.toHexStringNoPrefix(res)
                Log.d("hex",hex)

                tosign.add(sign[i])
                signatures.add(hex)
                pubkey.add(publicKey)


            }

            val sendtrans = BTCSignTransaction(tx,tosign,signatures,pubkey)
            val payload = Gson().toJson(sendtrans)
            val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(),payload)


            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://api.blockcypher.com/v1/$symbol/main/txs/send?token=")
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : okhttp3.Callback {
                    override fun onFailure(call: okhttp3.Call, e: IOException) {
                        activity?.runOnUiThread {
                            hideProgress()
                            Toast.makeText(activity,"Transaction failed",Toast.LENGTH_SHORT).show() }

                    }

                    override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                        val resp = response.body?.string()
                        val topic = Gson().fromJson(resp, BTCSendResponse::class.java)
                        Log.d("hash",topic.tx?.hash.toString())

                        activity?.runOnUiThread {
                            hideProgress()
                            edtSend.setText("")
                            edtReceive.setText("")
                            Toast.makeText(activity, getString(R.string.exchange_processed), Toast.LENGTH_SHORT).show()
                        }
                    }

                })

            } catch (e: Exception) {
                hideProgress()
                e.printStackTrace()
            }

        }
        catch (e:Exception){
            e.printStackTrace()
        }

    }

    private fun getMinAmount(){

        if (UtilsDefault.isOnline()){

            val inputParams = InputParams()
            inputParams.from_currency = fromCoin
            inputParams.to_currency = toCoin

            nodaWalletAPI.getminimum(inputParams).enqueue(object : Callback<MinimumBtcResponse>{
                override fun onFailure(call: Call<MinimumBtcResponse>, t: Throwable) {
                    hideProgress()
                    Log.d("fail","fal")
                }

                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<MinimumBtcResponse>, response: Response<MinimumBtcResponse>) {
                    minimumBtcResponse = response.body()

                    if (minimumBtcResponse?.status == 1){

                        if (minimumBtcResponse?.data!!.result !=null){
                            minvalue = minimumBtcResponse?.data?.result!!
                            val max = "200"

                            textMin?.text = " "+UtilsDefault.formatCryptoCurrency(minvalue)
                            textMax?.text = " "+max
                        }
                        else {
                            if (activity!=null){
                                Toast.makeText(activity,minimumBtcResponse?.data!!.error!!.message,Toast.LENGTH_SHORT).show()

                            }
                        }

                    }
                }
            })
        }
    }


    private fun UpdateValues() {
        var from = fromCoin
        var to  = toCoin


        for (i in 0..multicoinlist.size.minus(1)){

            if (from == multicoinlist[i].coinSymbol){

                reqfromAddress = multicoinlist[i].publicAddress
                if (multicoinlist[i].decimal !=""){
                    fromdecimal = multicoinlist[i].decimal.toDouble().toInt()
                }
                sendtype = multicoinlist[i].coinType

                textSendBalance?.text = " "+UtilsDefault.formatCryptoCurrency(multicoinlist[i].balance.toString())
                fromMarket =  multicoinlist[i].marketPrice

                try {
                    Picasso.with(context).load(multicoinlist[i].tokenImage).into(imgSend)
                }
                catch (e:Exception){
                    e.printStackTrace()
                }

                val min = multicoinlist[i].minex.toString()
                val max = multicoinlist[i].maxex.toString()
                val fees = UtilsDefault.formatCryptoCurrency(multicoinlist[i].fees.toString())
                myfees = fees
                UtilsDefault.updateSharedPreferenceString(Constants.MIN_EX,min)
                UtilsDefault.updateSharedPreferenceString(Constants.MAX_EX,max)
                textMin?.text = " "+min
                textMax?.text = " "+max
                textMaxSymbol?.text = " "+fromCoin
                textMinSymbol?.text = " "+fromCoin


            }
            else if (to == multicoinlist[i].coinSymbol){


                if (multicoinlist[i].decimal !=""){
                    todecimal = multicoinlist[i].decimal.toDouble().toInt()
                }

                textReceiveBalance?.text = " "+UtilsDefault.formatCryptoCurrency(multicoinlist[i].balance.toString())

                toMarket = multicoinlist[i].marketPrice
                receivetype = multicoinlist[i].coinType

                if (receivetype == "Token"){
                    reqtoAddress = credentials?.address!!
                }
                else {
                    reqtoAddress = multicoinlist[i].publicAddress
                }

                try {
                    if (multicoinlist[i].tokenImage!=""){
                        Picasso.with(context).load(multicoinlist[i].tokenImage).into(imgReceive)
                    }
                }
                catch (e:Exception){
                    e.printStackTrace()
                }

            }

        }

        var finalMarket = fromMarket.toDouble()/toMarket.toDouble()
        toValue = finalMarket.toString()
        textMarketPrice?.text = "1 $fromCoin = ${UtilsDefault.formatCryptoCurrency(finalMarket.toString())} $toCoin"

        updateData()

    }

    private fun updateData() {

        fromtemp.clear()

        for (i in 0..fromlist.size.minus(1)) {
            if (fromCoin != fromlist[i].coinSymbol) {
                fromtemp.add(
                    CoinList(
                        fromlist[i].coinName,
                        fromlist[i].coinImage,
                        fromlist[i].tokenImage,
                        fromlist[i].coinSymbol,
                        fromlist[i].phrase,
                        fromlist[i].coinType,
                        fromlist[i].publicAddress,
                        fromlist[i].privateKey,
                        fromlist[i].publicKey,
                        fromlist[i].enableStatus,
                        fromlist[i].balance,
                        fromlist[i].marketPrice,
                        fromlist[i].marketPercentage,
                        fromlist[i].decimal,
                        fromlist[i].network,
                        fromlist[i].minex,
                        fromlist[i].maxex,
                        fromlist[i].fees
                    )
                )
            }
        }

        totemp.clear()

        for (i in 0..tolist.size.minus(1)) {
            if (toCoin != tolist[i].coinSymbol) {
                totemp.add(
                    CoinList(
                        tolist[i].coinName,
                        tolist[i].coinImage,
                        tolist[i].tokenImage,
                        tolist[i].coinSymbol,
                        tolist[i].phrase,
                        tolist[i].coinType,
                        tolist[i].publicAddress,
                        tolist[i].privateKey,
                        tolist[i].publicKey,
                        tolist[i].enableStatus,
                        tolist[i].balance,
                        tolist[i].marketPrice,
                        tolist[i].marketPercentage,
                        tolist[i].decimal,
                        tolist[i].network,
                        tolist[i].minex,
                        tolist[i].maxex,
                        tolist[i].fees
                    )
                )
            }
        }
        getMinAmount()
    }


    private val generalTextWatcher = object : TextWatcher {

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun afterTextChanged(s: Editable?) {
            if (s != null && !s.toString().equals("", ignoreCase = true)) {

                if (edtSend.text.hashCode() == s.hashCode()) {
                    // This is just an example, your magic will be here!
                    val value = s.toString()
                    edtReceive.removeTextChangedListener(this)
                    if (value.isNotEmpty()) {
                        makeCalculation(value)
                        receiveAmount(value)
                    }
                    edtReceive.addTextChangedListener(this)


                } else if (edtReceive.text.hashCode() == s.hashCode()) {
                    // This is just an example, your magic will be here!
                    val value = s.toString()
                    edtSend.removeTextChangedListener(this)
                    if (value.isNotEmpty()) {
                        makeCalculation1(value)
                        receiveAmount(value)

                    }
                    edtSend.addTextChangedListener(this)
                }

            } else {

                edtReceive.text!!.clear()
                edtSend.text!!.clear()
                textFees.text = ""

            }

        }

    }

    private fun receiveAmount(value: String) {

        try {

            if (minvalue == ""){
                return
            }

            val myvalue = value.toDouble()
            val minval = minvalue.toDouble()
            if (myvalue < minval){
                textFees.text = ""
                return
            }

            if (UtilsDefault.isOnline()){

                val inputParams = InputParams()
                inputParams.from_currency = fromCoin
                inputParams.to_currency = toCoin
                inputParams.amount = value

                nodaWalletAPI.getreceiveaamount(inputParams).enqueue(object : Callback<MinimumBtcResponse>{
                    override fun onFailure(call: Call<MinimumBtcResponse>, t: Throwable) {

                    }

                    override fun onResponse(call: Call<MinimumBtcResponse>, response: Response<MinimumBtcResponse>) {

                        minimumBtcResponse = response.body()

                        if (minimumBtcResponse?.status == 1){
                            try {
                                textFees?.text = "Receive Amount : "+UtilsDefault.formatCryptoCurrency(minimumBtcResponse?.data?.result.toString())+" "+toCoin+" "+"(approx)"

                            }
                            catch (e:Exception){
                                e.printStackTrace()
                            }
                            /*val min = minimumBtcResponse?.data?.result
                            val max = "200"

                            textMin?.text = " "+min
                            textMax?.text = " "+max*/

                        }
                    }
                })
            }

        }
        catch (e:Exception){
            e.printStackTrace()
        }


    }

    private fun makeCalculation1(value: String) {

        try {
            val depo = value.toDouble() / toValue.toDouble()
            edtSend.setText(UtilsDefault.formatCryptoCurrency(depo.toString()))

           // feecalc()

        }
        catch (e:Exception){
            e.printStackTrace()
        }


        /*textReceiveAmount.text =
            "Receive Amount: " + UtilsDefault.formatCryptoCurrency((value.toDouble() - feeee).toString())*/

    }

    private fun makeCalculation(value: String) {


        try {
            val depo = value.toDouble() * toValue.toDouble()
            edtReceive.setText(UtilsDefault.formatCryptoCurrency(depo.toString()))

          //  feecalc()
        }
        catch (e:Exception){
            e.printStackTrace()
        }


    }


    private fun exchangeTrans() {
        if (UtilsDefault.isOnline()) {

            if (credentials != null) {
                doContractAsync {
                   /* if (getClientVersion()) {
                    }*/
                    if (sendtype == "Token" && receivetype == "Token"){
                      //  exchangeToken()
                        tokentotoken() //token to token
                    }
                    else if (sendtype == "Token"){
                        exchangeToken() // token to eth
                    }
                    else {
                        exchangeContract() // eth to token
                    }

                }.execute()
            }


        } else {
            Toast.makeText(activity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    private fun approveToken() {

        try {

            val contractAddress = ""
            val tokencontractAddress = reqfromAddress


            val gasPrice = BigInteger("20000000000")
            val gasLimit = BigInteger("25676")
            val gasProvider= StaticGasProvider(gasPrice,gasLimit)
            val sendamount = edtSend.text.toString().trim().replace(",",".")

            val tkndecimal = BigDecimal.TEN.pow(6)
            val value = tkndecimal * (sendamount).toBigDecimal()

            val feedepo = myfees

            feehistory = feedepo

            val amountDouble = BigDecimal.valueOf(feedepo.toDouble())
            val convertedAmount = Convert.toWei(amountDouble, Convert.Unit.ETHER)

            val transactionReceiptProcessor = NoOpProcessor(web3)
            val nonce = web3!!.ethGetTransactionCount(credentials!!.address, DefaultBlockParameterName.LATEST).send().transactionCount

            val transactionManager = RawTransactionManager(
                web3!!,
                credentials,
                Constants.CHAINID,
                transactionReceiptProcessor
            )

            val encodedFunction = FunctionEncoder.encode(approveToken(contractAddress,value.toBigInteger()))
            val rawTransaction = RawTransaction.createTransaction(
                nonce,
                gasPrice,
                gasLimit,
                tokencontractAddress,
                convertedAmount.toBigInteger(),
                encodedFunction
            )
            val sendTransaction = transactionManager.signAndSend(rawTransaction)
            hideProgress()

            Log.d(
                "WEB3",
                "Approve transaction " + sendTransaction.transactionHash + " Str " + sendTransaction.toString()
            )

        }
        catch (e:Exception){
            activity?.runOnUiThread {
                hideProgress()
                edtSend.setText("")
                edtReceive.setText("")

                Toast.makeText(
                    activity,
                    getString(R.string.exchange_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
            e.printStackTrace()
        }

    }

    private fun tokentotoken(){
        try {

            val receive = edtReceive.text.toString().replace(",",".").trim()
            if (adminBalance < receive.toDouble()){
                activity?.runOnUiThread {
                    hideProgress()
                    Toast.makeText(activity,"Admin balance not available..",Toast.LENGTH_SHORT).show()
                }
                return
            }
            else {
            }


            // contract
            val contractAddress = Constants.CONTRACTADDR
            val tokencontractAddress = reqfromAddress

            //gasprices
            val gasPrice = BigInteger("30000000000")
            val gasLimit = BigInteger("347578")
            val gasProvider= StaticGasProvider(gasPrice,gasLimit)

            //values to send
            val sendamount = edtSend.text.toString().trim().replace(",",".")
            val tkndecimal = BigDecimal.TEN.pow(fromdecimal)
            val value = tkndecimal * (sendamount).toBigDecimal()

            val feedepo = myfees
            feehistory = feedepo

            //fees
            val amountDouble = BigDecimal.valueOf(feedepo.toDouble())
            val convertedAmount = Convert.toWei(amountDouble, Convert.Unit.ETHER)

            //erc approval
            val erc= ERC20.load(tokencontractAddress,web3!!,credentials,gasProvider)
            val tranReceipt=erc.approve(contractAddress,value.toBigIntegerExact()).send()

            if (tranReceipt.status == "0x1"){
                val transactionReceiptProcessor = NoOpProcessor(web3)
                val nonce = web3!!.ethGetTransactionCount(credentials!!.address, DefaultBlockParameterName.LATEST).send().transactionCount

                val transactionManager = RawTransactionManager(
                    web3!!,
                    credentials,
                    Constants.CHAINID,
                    transactionReceiptProcessor
                )


                val sdf = SimpleDateFormat("yyyyMMddHHmmss",Locale.getDefault())
                val time = sdf.format(Date())
                val bigtime = BigInteger(time)

                val send = BigDecimal.TEN.pow(18)
                val receivedecimal = BigDecimal.TEN.pow(fromdecimal)
                val pricedecimal = BigDecimal.TEN.pow(todecimal)
                val qua = edtSend.text.toString().trim().replace(",",".")
                val quanty = receivedecimal * (qua).toBigDecimal()
                price = fromMarket.toDouble()/toMarket.toDouble()

                val  priceamout = pricedecimal * (price).toBigDecimal()
                val fee = send * (myfees).toBigDecimal()


                val mm = message
                val data = Numeric.hexStringToByteArray(mm)
                val vvv = Numeric.cleanHexPrefix(v).toLong(16)
                val rr = Numeric.hexStringToByteArray(r)
                val  ss = Numeric.hexStringToByteArray(s)


                val encodedFunction = FunctionEncoder.encode(TokenToTokenEx(bigtime,quanty,priceamout,fee,data,vvv,rr,ss))
                val rawTransaction = RawTransaction.createTransaction(
                    nonce,
                    gasPrice,
                    gasLimit,
                    contractAddress,
                    convertedAmount.toBigInteger(),
                    encodedFunction
                )
                val sendTransaction = transactionManager.signAndSend(rawTransaction)

                Log.d("WEB3", "Exchange transaction " + sendTransaction.transactionHash)

                if (sendTransaction.transactionHash != null) {
                    activity?.runOnUiThread {
                        hideProgress()
                        Toast.makeText(activity, getString(R.string.exchange_processed), Toast.LENGTH_SHORT).show()
                    }

                } else {
                    activity?.runOnUiThread {
                        hideProgress()
                        Toast.makeText(activity, getString(R.string.exchange_failed), Toast.LENGTH_SHORT).show()
                    }
                    Log.d("exchange", "failed")

                }

            }

        }
        catch (e:Exception){
            activity?.runOnUiThread {
                hideProgress()
                edtSend.setText("")
                edtReceive.setText("")

                Toast.makeText(
                    activity,
                    getString(R.string.exchange_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
            e.printStackTrace()
        }

    }

    private fun exchangeToken() {

        try {

            val receive = edtReceive.text.toString().replace(",",".").trim()
            if (adminBalance < receive.toDouble()){
                activity?.runOnUiThread {
                    hideProgress()
                    Toast.makeText(activity,"Admin balance not available...",Toast.LENGTH_SHORT).show()
                }
                return
            }
            else {
            }


            // contract
            val contractAddress = Constants.CONTRACTADDR
            val tokencontractAddress = reqfromAddress

            //gasprices
            val gasPrice = BigInteger("20000000000")
            val gasLimit = BigInteger("347578")
            val gasProvider= StaticGasProvider(gasPrice,gasLimit)

            //values to send
            val sendamount = edtSend.text.toString().trim().replace(",",".")
            val tkndecimal = BigDecimal.TEN.pow(fromdecimal)
            val value = tkndecimal * (sendamount).toBigDecimal()

            val feedepo = myfees
            feehistory = feedepo

            //fees
            val amountDouble = BigDecimal.valueOf(feedepo.toDouble())
            val convertedAmount = Convert.toWei(amountDouble, Convert.Unit.ETHER)



            //erc approval
            val erc= ERC20.load(tokencontractAddress,web3!!,credentials,gasProvider)
            val tranReceipt=erc.approve(contractAddress,value.toBigIntegerExact()).send()
            Log.v("WEB3", "approvetransactioin " + tranReceipt.transactionHash)

            if (tranReceipt.status == "0x1"){
                val transactionReceiptProcessor = NoOpProcessor(web3)
                val nonce = web3!!.ethGetTransactionCount(credentials!!.address, DefaultBlockParameterName.LATEST).send().transactionCount

                val transactionManager = RawTransactionManager(
                    web3!!,
                    credentials,
                    Constants.CHAINID,
                    transactionReceiptProcessor
                )


                val sdf = SimpleDateFormat("yyyyMMddHHmmss",Locale.getDefault())
                val time = sdf.format(Date())
                val bigtime = BigInteger(time)

                val send = BigDecimal.TEN.pow(18)
                val receivedecimal = BigDecimal.TEN.pow(fromdecimal)
                val qua = edtSend.text.toString().trim().replace(",",".")
                val quanty = receivedecimal * (qua).toBigDecimal()
                price = fromMarket.toDouble()/toMarket.toDouble()

                val  priceamout = receivedecimal * (price).toBigDecimal()
                val fee = send * (myfees).toBigDecimal()


                val mm = message
                val data = Numeric.hexStringToByteArray(mm)
                val vvv = Numeric.cleanHexPrefix(v).toLong(16)
                val rr = Numeric.hexStringToByteArray(r)
                val  ss = Numeric.hexStringToByteArray(s)


                val encodedFunction = FunctionEncoder.encode(exchangeTokenfun(bigtime,quanty,priceamout,fee,data,vvv,rr,ss))
                val rawTransaction = RawTransaction.createTransaction(
                    nonce,
                    gasPrice,
                    gasLimit,
                    contractAddress,
                    convertedAmount.toBigInteger(),
                    encodedFunction
                )
                val sendTransaction = transactionManager.signAndSend(rawTransaction)

                Log.d("WEB3", "Exchange transaction " + sendTransaction.transactionHash)

                if (sendTransaction.transactionHash != null) {
                    activity?.runOnUiThread {
                        hideProgress()
                        Toast.makeText(activity, getString(R.string.exchange_processed), Toast.LENGTH_SHORT).show()
                    }

                } else {
                    activity?.runOnUiThread {
                        hideProgress()
                        Toast.makeText(activity, getString(R.string.exchange_failed), Toast.LENGTH_SHORT).show()
                    }
                    Log.d("exchange", "failed")

                }

            }

        }
        catch (e:Exception){
            activity?.runOnUiThread {
                hideProgress()
                edtSend.setText("")
                edtReceive.setText("")

                Toast.makeText(
                    activity,
                    getString(R.string.exchange_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
            e.printStackTrace()
        }


    }

    private fun feecalc(){

        if (fromCoin == "ETH"){

            val s = BigDecimal.TEN.pow(fromdecimal)
            val tkndecimal = BigDecimal.TEN.pow(todecimal)
            val qua = edtReceive.text.toString().replace(",",".").trim()
            val formatquanty = UtilsDefault.formatToken(qua)
            val quanty = tkndecimal * (formatquanty).toBigDecimal()
            price = toMarket.toDouble()/fromMarket.toDouble()
            val priceamou = UtilsDefault.formatCryptoCurrency(price.toString())
            val  priceamout = s * (priceamou).toBigDecimal()
            val fee = s * (myfees).toBigDecimal()


            val myquant = quanty.toDouble()/todecimal
            val myprice = Convert.fromWei(priceamout,Convert.Unit.ETHER)
            val myfees =  Convert.fromWei(fee,Convert.Unit.ETHER)


            val pricecalc = myprice*myquant.toBigDecimal()
            val feecalc = pricecalc+myfees

            val send = edtSend.text.toString().trim().replace(",",".").toDouble()
            val finalfees = feecalc.toDouble()-send

            textFees.text = "Fees " + myfees + "" + " (" + UtilsDefault.formatCryptoCurrency(finalfees.toString()) + ")"

            calcfees = ""+UtilsDefault.formatCryptoCurrency(finalfees.toString())
        }
        else if (fromCoin == "BTC"){

        }
        else {

            val feedepo = myfees.toFloat()
            textFees.text = "Fees " +myfees+ "" + " (" + UtilsDefault.formatCryptoCurrency(feedepo.toString()) + ")"
            calcfees = ""+UtilsDefault.formatCryptoCurrency(feedepo.toString())


        }

    }

    private fun checkBalance(tokenadd:String){

        if (UtilsDefault.isOnline()){

            val contract = Constants.CONTRACTADDR
            var passurl = ""
            var balance = ""

            if (receivetype == "Token"){

                passurl = "${Constants.API_ETHERSCAN}?module=account&action=tokenbalance&contractaddress=$tokenadd&address=$contract&tag=latest&apikey=${Constants.APIKEY}"
            }
            else {
                passurl = "${Constants.API_ETHERSCAN}?module=account&action=balance&address=$contract&tag=latest&apikey=${Constants.APIKEY}"
            }

            var balanceResponse:BalanceResponse? = null

            nodaWalletAPI.getbalance(passurl).enqueue(object : Callback<BalanceResponse> {
                override fun onFailure(call: Call<BalanceResponse>, t: Throwable) {

                }
                override fun onResponse(
                    call: Call<BalanceResponse>,
                    response: Response<BalanceResponse>
                ) {
                    balanceResponse = response.body()
                    if (balanceResponse != null) {
                        if (balanceResponse?.status == "1") {
                            balance = balanceResponse?.result.toString()
                            val numner = todecimal
                            val add = numner.toDouble()
                            val add2= add.toInt()
                            val nu = add2+1
                            val formatted = String.format("%1$-" + nu + "s", "1").replace(' ', '0');

                            adminBalance  = balance.toDouble() / formatted.toDouble()

                        }

                    }
                }

            })

        }
    }

    private fun exchangeContract() {

        try {

            //contract

            val receive = edtReceive.text.toString().replace(",",".").trim()
            if (adminBalance < receive.toDouble()){
                activity?.runOnUiThread {
                    hideProgress()
                    Toast.makeText(activity,"Admin balance not available...",Toast.LENGTH_SHORT).show()
                }
                return
            }
            else {
            }


            val contractAddress = Constants.CONTRACTADDR
            val nonce = web3!!.ethGetTransactionCount(credentials!!.address, DefaultBlockParameterName.LATEST).send().transactionCount

            //gasprice
            val gasPrice = BigInteger("20000000000")
            val gasLimit = BigInteger("380908")

            //fees and values
            val send = BigDecimal.TEN.pow(fromdecimal)
            val tkndecimal = BigDecimal.TEN.pow(todecimal)
            val qua = edtReceive.text.toString().replace(",",".").trim()
            val formatquanty = UtilsDefault.formatToken(qua).replace(",",".").trim()
            val quanty = tkndecimal * (formatquanty).toBigDecimal()
            price = toMarket.toDouble()/fromMarket.toDouble()
            val priceamou = UtilsDefault.formatCryptoCurrency(price.toString()).replace(",",".").trim()
            val  priceamout = send * (priceamou).toBigDecimal()
            val fee = send * (myfees).toBigDecimal()

            val myprice = Convert.fromWei(priceamout,Convert.Unit.ETHER)
            val myfees =  Convert.fromWei(fee,Convert.Unit.ETHER)



            val pricecalc = myprice*formatquanty.toBigDecimal()
            val feecalc = pricecalc+myfees
            feehistory = feecalc.toString()

            val final = Convert.toWei(feecalc,Convert.Unit.ETHER)


            val transactionReceiptProcessor = NoOpProcessor(web3)
            val transactionManager = RawTransactionManager(
                web3!!,
                credentials!!,
                Constants.CHAINID,
                transactionReceiptProcessor
            )


            val time = System.currentTimeMillis().toString()
            val bigtime = BigInteger(time)


            val mm = message
            val data = Numeric.hexStringToByteArray(mm)
            val vvv = Numeric.cleanHexPrefix(v).toLong(16)
            val rr = Numeric.hexStringToByteArray(r)
            val  ss = Numeric.hexStringToByteArray(s)

            val encodedFunction = FunctionEncoder.encode(exchangecontract(bigtime,quanty,priceamout,fee,data,vvv,rr,ss))
            val rawTransaction = RawTransaction.createTransaction(
                nonce,
                gasPrice,
                gasLimit,
                contractAddress,
                final.toBigInteger(),
                encodedFunction
            )


            val sendTransaction = transactionManager.signAndSend(rawTransaction)

            Log.d(
                "WEB3",
                "Exchange transaction " + sendTransaction.transactionHash + " Str " + sendTransaction.toString()
            )

            if (sendTransaction.transactionHash != null) {
                activity?.runOnUiThread {
                    val receive = edtReceive.text.toString().trim().replace(",",".")

                    hideProgress()

                    Toast.makeText(activity, getString(R.string.exchange_processed), Toast.LENGTH_SHORT).show()



                }

                Log.d("exchange", "success")
            } else {
                activity?.runOnUiThread {
                    hideProgress()

                        Toast.makeText(
                            activity,
                            getString(R.string.exchange_failed),
                            Toast.LENGTH_SHORT
                        ).show()



                }
                Log.d("exchange", "failed")

            }

        }
        catch (e:Exception){

            activity?.runOnUiThread {
                hideProgress()
                edtSend.setText("")
                edtReceive.setText("")

                Toast.makeText(
                    activity,
                    getString(R.string.exchange_failed),
                    Toast.LENGTH_SHORT).show()

            }
            e.printStackTrace()
        }



    }



    fun approveToken(toAddr: String, amount: BigInteger): Function {
        val function = Function(
            "approve",//totalDeposit
            Arrays.asList(
                Address(toAddr),
                Uint256(amount)
            ) as List<Type<Any>>?,
            emptyList()
        )
        return function
    }

    fun exchangecontract(
        bigtime: BigInteger,
        quanty: BigDecimal,
        priceamout: BigDecimal,
        fee: BigDecimal,
        msg: ByteArray,
        v: Long,
        r: ByteArray,
        s: ByteArray
    ): Function {

        val function = Function(
            "exchange",Arrays.asList( Uint128(bigtime), //orderid
                Uint256(quanty.toBigInteger())) as List<Type<Any>>?,
            emptyList()
           /* Arrays.asList(
                Uint128(bigtime), //orderid
                Uint256(quanty.toBigInteger()), //quantity 100000000000000000
                Uint128(priceamout.toBigInteger()), // market price 10000000000000000
                Uint128(fee.toBigInteger()), //fees 1000000000000000
                Address("0x0000000000000000000000000000000000000000"), //token add
                Address(reqtoAddress), //req add
                Address("0x0000000000000000000000000000000000000000"),
                Uint8(v),
                Bytes32(msg),
                Bytes32(r),
                Bytes32(s)
            ) as List<Type<Any>>?,
            emptyList()*/
        )

        return function
    }

    fun TokenToTokenEx(
        bigtime: BigInteger,
        quanty: BigDecimal,
        priceamout: BigDecimal,
        fee: BigDecimal,
        msg: ByteArray,
        vvv: Long,
        r: ByteArray,
        s: ByteArray) : Function{

        val function = Function(
            "exchange",//exchange
            Arrays.asList(
                Uint128(bigtime), //orderid
                Uint256(quanty.toBigInteger()), //quantity 100000000000000000
                Uint128(priceamout.toBigInteger()), // market price 1000000000000000000
                Uint128(fee.toBigInteger()), //fees 1000000000000000
                Address(reqfromAddress), //token add
                Address(reqtoAddress), //req add
                Address("0x0000000000000000000000000000000000000000"), // fee add
                Uint8(vvv),
                Bytes32(msg),
                Bytes32(r),
                Bytes32(s)
            ) as List<Type<Any>>?,
            emptyList()
        )
        return function

    }

    fun exchangeTokenfun(
        bigtime: BigInteger,
        quanty: BigDecimal,
        priceamout: BigDecimal,
        fee: BigDecimal,
        msg: ByteArray,
        vvv: Long,
        r: ByteArray,
        s: ByteArray
    ): Function {

        val function = Function(
            "exchange",//exchange
            Arrays.asList(
                Uint128(bigtime), //orderid
                Uint256(quanty.toBigInteger()), //quantity 100000000000000000
                Uint128(priceamout.toBigInteger()), // market price 1000000000000000000
                Uint128(fee.toBigInteger()), //fees 1000000000000000
                Address(reqfromAddress), //token add
                Address("0x0000000000000000000000000000000000000000"), //req add
                Address("0x0000000000000000000000000000000000000000"), // fee add
                Uint8(vvv),
                Bytes32(msg),
                Bytes32(r),
                Bytes32(s)
            ) as List<Type<Any>>?,
            emptyList()
        )
        return function
    }

    private fun initCredentials() {

        web3 = Web3j.build(HttpService(Constants.INFURA))
         val mnemonic = UtilsDefault.getSharedPreferenceString(Constants.PHRASE)
        credentials = Credentials.create(mnemonic)

        val end = UtilsDefault.getSharedPreferenceString(Constants.BNBKEY)
        wallet = Wallet(end, BinanceDexEnvironment.PROD)

    }

    class doContractAsync(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            handler()
            return null
        }
    }


    override fun onResume() {
        super.onResume()
        mainBack = 1
    }

}

