package com.application.nodawallet.fragment

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.nodawallet.R
import com.application.nodawallet.activity.MainActivity
import com.application.nodawallet.activity.MainActivity.Companion.mainBack
import com.application.nodawallet.activity.ReceiveActivity
import com.application.nodawallet.activity.SendActivity
import com.application.nodawallet.adapter.CurrencyinfoAdapter
import com.application.nodawallet.application.NodaApplication
import com.application.nodawallet.model.*
import com.application.nodawallet.retrofit.NodaWalletAPI
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.UtilsDefault
import com.application.nodawallet.viewmodel.HistoryViewModel
import com.application.nodawallet.viewmodel.MultiCoinViewModel
import com.binance.dex.api.client.BinanceDexApiCallback
import com.binance.dex.api.client.BinanceDexApiClientFactory
import com.binance.dex.api.client.BinanceDexEnvironment
import com.binance.dex.api.client.domain.TransactionPage
import com.binance.dex.api.client.domain.TransactionType
import com.binance.dex.api.client.domain.request.TransactionsRequest
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_currency_detail.*
import org.web3j.crypto.Credentials
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class CurrencyDetailFragment : BaseFragment(R.layout.fragment_currency_detail) {

    @Inject
    lateinit var nodaWalletAPI: NodaWalletAPI

    private lateinit var viewCoinmodel: MultiCoinViewModel
    val multicoinlist = ArrayList<CoinList>()
    var transactionHistoryResponse: TransactionHistoryResponse? = null
    var btcHistoryResponse: BtcHistoryResponse? = null
    private var currencyInfoAdapter: CurrencyinfoAdapter? = null
    var walletId = 0
    var pubadress = ""
    var coinName = ""
    var network = ""
    var coinSymbol = ""
    var marketPrice = ""
    var marketPercentage = ""
    var balance = ""
    var cointype = ""
    var decimal = ""
    var key = ""
    var ethaddress = ""
    var bnbaddress = ""
    var isTransHistory = false
    var isChartSet = false
    var marketChartResponse: MarketChartResponse? = null
    val charValue: ArrayList<Entry> = ArrayList()
    val xAxis: ArrayList<String> = ArrayList()
    private lateinit var historyViewModel: HistoryViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NodaApplication.instance.mComponent.inject(this)
        historyViewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)


        val bundle = arguments
        if (bundle != null) {
            coinName = bundle.getString("coinName") ?: ""
            marketPrice = bundle.getString("marketPrice") ?: ""
            marketPercentage = bundle.getString("marketPercentage") ?: ""
        }

        chartGraph.setNoDataText(getString(R.string.loading_chart))



        mRecycle_info.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        currencyInfoAdapter = CurrencyinfoAdapter(activity!!)
        mRecycle_info!!.adapter = currencyInfoAdapter
        viewCoinmodel = ViewModelProvider(this).get(MultiCoinViewModel::class.java)


        try {
            viewCoinmodel.getDataById.removeObservers(activity!!)
            viewCoinmodel.getDataById.observe(activity!!,
                Observer<List<MultiCoinList>> { coinlist ->

                    if (coinlist.isNotEmpty() && coinlist[0].list.isNotEmpty()) {

                        multicoinlist.clear()
                        for (i in 0..coinlist[0].list.size.minus(1)) {
                            ethaddress = coinlist[0].list[0].publicAddress
                            bnbaddress = coinlist[0].list[3].publicAddress
                            if (coinlist[0].list[i].coinName == coinName) {

                                balance = coinlist[0].list[i].balance
                                coinSymbol = coinlist[0].list[i].coinSymbol
                                network = coinlist[0].list[i].network
                                cointype = coinlist[0].list[i].coinType
                                if (cointype == "BEP2") {
                                    textHeader?.text = coinName.toUpperCase()
                                    textBalance?.text = UtilsDefault.formatCryptoCurrency(coinlist[0].list[i].balance) + " " + coinName.toUpperCase()

                                } else {
                                    textHeader?.text = coinlist[0].list[i].coinSymbol
                                    textBalance?.text = UtilsDefault.formatCryptoCurrency(coinlist[0].list[i].balance) + " " + coinlist[0].list[i].coinSymbol

                                }

                                val currency = UtilsDefault.getSharedPreferenceString(Constants.CURRENCY)
                                val symbol = currency?.split("-")!![1]
                                val mark = coinlist[0].list[i].marketPrice
                                if (mark != "null") {
                                    textMarketPrice?.text = UtilsDefault.formatToken(mark) + " " + symbol
                                    textGraphvalue?.text = UtilsDefault.formatToken(mark) + " " + symbol
                                }
                                val marketPercent = coinlist[0].list[i].marketPercentage
                                if (marketPercent == "null") {
                                    textMarketPercentage?.text = ""
                                } else {
                                    textMarketPercentage?.text = UtilsDefault.formatDecimal(marketPercent) + " %"

                                    val market = coinlist[0].list[i].marketPercentage.toDouble()
                                    if (market >= 0) {
                                        textMarketPercentage?.setTextColor(ContextCompat.getColor(activity!!, R.color.stockgreen))
                                    } else {
                                        textMarketPercentage?.setTextColor(
                                            ContextCompat.getColor(
                                                activity!!,
                                                R.color.stockred
                                            )
                                        )

                                    }
                                }

                                try {
                                    Picasso.with(context).load(coinlist[0].list[i].tokenImage).into(imgCurrency)

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }


                                pubadress = coinlist[0].list[i].publicAddress
                                key = coinlist[0].list[i].privateKey
                                decimal = coinlist[0].list[i].decimal


                                if (!isTransHistory) {

                                    if (coinSymbol == "BTC" || coinSymbol == "LTC") {
                                        btcHistory(pubadress, coinSymbol)
                                    } else if (coinSymbol == "BNB" || cointype == "BEP2") {
                                        bnbHistory(pubadress)
                                    } else {
                                        if (cointype == "token" || cointype == "Token") {
                                            val credentials = Credentials.create(key)
                                            getTransHistory(credentials.address, pubadress)

                                        } else {

                                            if (cointype == "public") {
                                                getTransHistory(pubadress, "")
                                            } else {
                                                val credentials = Credentials.create(key)
                                                getTransHistory(credentials.address, "")
                                            }

                                        }
                                    }

                                }

                            }
                        }
                        walletId = coinlist[0].id
                    }

                })

        } catch (e: Exception) {
            e.printStackTrace()
        }



        llSend.setOnClickListener {

            val cointype2 = UtilsDefault.getSharedPreferenceString(Constants.COINTYPE)
            if (cointype2 == "public") {
                Toast.makeText(
                    activity,
                    getString(R.string.transaction_publicaddress),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val intent = Intent(activity!!, SendActivity::class.java)
            intent.putExtra("key", key)
            intent.putExtra("balance", balance)
            intent.putExtra("cointype", cointype)
            intent.putExtra("address", pubadress)
            intent.putExtra("decimal", decimal)
            intent.putExtra("coinsymbol", coinSymbol)
            intent.putExtra("marketprice", marketPrice)
            startActivity(intent)

        }


        llReceive.setOnClickListener {

            var myadd = ""
            if (coinSymbol == "BTC" || coinSymbol == "ETH" || coinSymbol == "LTC" || coinSymbol == "BNB") {
                myadd = pubadress
            } else if (cointype == "BEP2") {
                myadd = bnbaddress
            } else if (cointype == "Token") {
                myadd = ethaddress
            }


            val intent = Intent(activity!!, ReceiveActivity::class.java)
            val mykey = myadd
            intent.putExtra("address", mykey)
            intent.putExtra("coinsymbol", coinSymbol)
            startActivity(intent)

        }

        imgCurrencyDetail.setOnClickListener {
            (context as MainActivity).push(HomeFragment())
        }

        llCopyAdd.setOnClickListener {
            val myadd = UtilsDefault.getSharedPreferenceString(Constants.MYADDRESS)
            UtilsDefault.copyToClipboard(activity!!, myadd.toString())
            Toast.makeText(activity, getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
        }

        chip24.setOnClickListener {

            isChartSet = false

            chip24.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            chip24.chipBackgroundColor =
                ColorStateList.valueOf(ContextCompat.getColor(activity!!, R.color.bottomactive))

            chipWeek.setTextColor(ContextCompat.getColor(activity!!, R.color.bottomactive))
            chipWeek.chipBackgroundColor =
                ColorStateList.valueOf(ContextCompat.getColor(activity!!, R.color.graphback))

            chipMonth.setTextColor(ContextCompat.getColor(activity!!, R.color.bottomactive))
            chipMonth.chipBackgroundColor =
                ColorStateList.valueOf(ContextCompat.getColor(activity!!, R.color.graphback))

            chipYear.setTextColor(ContextCompat.getColor(activity!!, R.color.bottomactive))
            chipYear.chipBackgroundColor =
                ColorStateList.valueOf(ContextCompat.getColor(activity!!, R.color.graphback))


            val current = System.currentTimeMillis()
            val toTime = (current / 1000).toString()

            val cal = Calendar.getInstance()
            cal.add(Calendar.DATE, -1)
            val result = cal.timeInMillis

            val from = (result / 1000).toString()

            getMarketChart(from, toTime)


        }

        chipMonth.setOnClickListener {
            isChartSet = false

            defaultMarket()

        }

        chipWeek.setOnClickListener {
            isChartSet = false

            chip24.setTextColor(ContextCompat.getColor(activity!!, R.color.bottomactive))
            chip24.chipBackgroundColor =
                ColorStateList.valueOf(ContextCompat.getColor(activity!!, R.color.graphback))

            chipWeek.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            chipWeek.chipBackgroundColor =
                ColorStateList.valueOf(ContextCompat.getColor(activity!!, R.color.bottomactive))

            chipMonth.setTextColor(ContextCompat.getColor(activity!!, R.color.bottomactive))
            chipMonth.chipBackgroundColor =
                ColorStateList.valueOf(ContextCompat.getColor(activity!!, R.color.graphback))

            chipYear.setTextColor(ContextCompat.getColor(activity!!, R.color.bottomactive))
            chipYear.chipBackgroundColor =
                ColorStateList.valueOf(ContextCompat.getColor(activity!!, R.color.graphback))

            val current = System.currentTimeMillis()
            val toTime = (current / 1000).toString()

            val cal = Calendar.getInstance()
            cal.add(Calendar.WEEK_OF_MONTH, -1)
            val result = cal.timeInMillis

            val from = (result / 1000).toString()

            getMarketChart(from, toTime)
        }

        chipYear.setOnClickListener {
            isChartSet = false

            chip24.setTextColor(ContextCompat.getColor(activity!!, R.color.bottomactive))
            chip24.chipBackgroundColor =
                ColorStateList.valueOf(ContextCompat.getColor(activity!!, R.color.graphback))

            chipWeek.setTextColor(ContextCompat.getColor(activity!!, R.color.bottomactive))
            chipWeek.chipBackgroundColor =
                ColorStateList.valueOf(ContextCompat.getColor(activity!!, R.color.graphback))

            chipMonth.setTextColor(ContextCompat.getColor(activity!!, R.color.bottomactive))
            chipMonth.chipBackgroundColor =
                ColorStateList.valueOf(ContextCompat.getColor(activity!!, R.color.graphback))

            chipYear.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            chipYear.chipBackgroundColor =
                ColorStateList.valueOf(ContextCompat.getColor(activity!!, R.color.bottomactive))

            val current = System.currentTimeMillis()
            val toTime = (current / 1000).toString()

            val cal = Calendar.getInstance()
            cal.add(Calendar.YEAR, -1)
            val result = cal.timeInMillis

            val from = (result / 1000).toString()


            getMarketChart(from, toTime)
        }

    }

    private fun bnbHistory(pubadress: String) {
        val client = BinanceDexApiClientFactory.newInstance().newAsyncRestClient(BinanceDexEnvironment.PROD.baseUrl)
        val txnRequenst = TransactionsRequest()
        txnRequenst.endTime = System.currentTimeMillis()
        txnRequenst.address = pubadress
        if (coinSymbol == "BNB") {
            txnRequenst.txType = TransactionType.TRANSFER
        }
        client.getTransactions(txnRequenst, object : BinanceDexApiCallback<TransactionPage> {
            override fun onResponse(response: TransactionPage?) {
                val bnbhistory = response?.tx
                if (bnbhistory!!.isNotEmpty()) {
                    textLoading?.visibility = View.GONE

                    val historylist: ArrayList<TransactionHistory> = ArrayList()
                    historylist.clear()

                    for (i in 0..bnbhistory.size.minus(1)) {

                        if (bnbhistory[i].txAsset == null) {
                            if (bnbhistory[i].txAsset == coinSymbol && bnbhistory[i].txAsset != "BNB") {
                                val json = Gson().fromJson<BnbOrderData>(bnbhistory[i].data)
                                if (json.orderData!!.symbol!!.contains(coinSymbol)) {
                                    historylist.add(
                                        TransactionHistory(
                                            bnbhistory[i].txHash,
                                            bnbhistory[i].timeStamp.toString(),
                                            bnbhistory[i].txType,
                                            "8",
                                            "",
                                            bnbhistory[i].fromAddr,
                                            bnbhistory[i].toAddr,
                                            bnbhistory[i].value,
                                            bnbhistory[i].txFee,
                                            bnbhistory[i].data,
                                            "",
                                            "",
                                            "" + bnbhistory[i].txAsset
                                        )
                                    )
                                }
                            }

                        } else {
                            if (bnbhistory[i].txAsset == coinSymbol) {
                                Log.d("txtype", bnbhistory[i].txType)
                                historylist.add(
                                    TransactionHistory(
                                        bnbhistory[i].txHash,
                                        bnbhistory[i].timeStamp.toString(),
                                        bnbhistory[i].txType,
                                        "8",
                                        "",
                                        bnbhistory[i].fromAddr,
                                        bnbhistory[i].toAddr,
                                        bnbhistory[i].value,
                                        bnbhistory[i].txFee,
                                        bnbhistory[i].data,
                                        "",
                                        "",
                                        "" + bnbhistory[i].txAsset
                                    )
                                )
                            }
                        }


                    }

                    currencyInfoAdapter?.sethistory(
                        historylist,
                        pubadress,
                        coinSymbol,
                        cointype,
                        ethaddress
                    )
                } else {
                    textLoading?.text = getString(R.string.no_transactions)
                }
                defaultMarket()

            }

            override fun onFailure(cause: Throwable?) {
                super.onFailure(cause)
                textLoading?.text = getString(R.string.no_transactions)
                defaultMarket()


            }
        })
    }


    private fun defaultMarket() {

        chip24?.setTextColor(ContextCompat.getColor(activity!!, R.color.bottomactive))
        chip24?.chipBackgroundColor =
            ColorStateList.valueOf(ContextCompat.getColor(activity!!, R.color.graphback))

        chipWeek?.setTextColor(ContextCompat.getColor(activity!!, R.color.bottomactive))
        chipWeek?.chipBackgroundColor =
            ColorStateList.valueOf(ContextCompat.getColor(activity!!, R.color.graphback))

        chipMonth?.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
        chipMonth?.chipBackgroundColor =
            ColorStateList.valueOf(ContextCompat.getColor(activity!!, R.color.bottomactive))

        chipYear?.setTextColor(ContextCompat.getColor(activity!!, R.color.bottomactive))
        chipYear?.chipBackgroundColor =
            ColorStateList.valueOf(ContextCompat.getColor(activity!!, R.color.graphback))

        val current = System.currentTimeMillis()
        val toTime = (current / 1000).toString()

        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, -1)
        val result = cal.timeInMillis

        val from = (result / 1000).toString()

        getMarketChart(from, toTime)
    }


    private fun btcHistory(address: String, coinsymbol: String) {
        if (UtilsDefault.isOnline()) {

            var url = ""

            if (coinsymbol == "BTC") {
                url = "https://sochain.com/api/v2/address/BTC/$address"
            } else {
                url = "https://sochain.com/api/v2/address/LTC/$address"
            }

            nodaWalletAPI.getbtchistory(url).enqueue(object :
                Callback<BtcHistoryResponse> {
                override fun onFailure(call: Call<BtcHistoryResponse>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<BtcHistoryResponse>,
                    response: Response<BtcHistoryResponse>
                ) {
                    btcHistoryResponse = response.body()
                    if (btcHistoryResponse?.data != null) {

                        if (btcHistoryResponse?.data!!.txs!!.isNotEmpty()) {
                            textLoading?.visibility = View.GONE

                            val historylist: ArrayList<TransactionHistory> = ArrayList()
                            historylist.clear()


                            for (i in 0..btcHistoryResponse?.data!!.txs!!.size.minus(1)) {
                                if (btcHistoryResponse?.data!!.txs!![i].outgoing != null && btcHistoryResponse?.data!!.txs!![i].incoming != null) {
                                    var sendinp =
                                        btcHistoryResponse?.data!!.txs!![i].incoming!!.value!!.toDouble()
                                    var sendout =
                                        btcHistoryResponse?.data!!.txs!![i].outgoing!!.value!!.toDouble()
                                    var final = sendout - sendinp

                                    historylist.add(
                                        TransactionHistory(
                                            btcHistoryResponse?.data!!.txs!![i].txid,
                                            btcHistoryResponse?.data!!.txs!![i].time.toString(),
                                            "Sent",
                                            "8",
                                            "",
                                            "",
                                            btcHistoryResponse?.data!!.txs!![i].outgoing!!.outputs!![0].address,
                                            UtilsDefault.formatCryptoCurrency(final.toString()),
                                            btcHistoryResponse?.data!!.txs!![i].outgoing!!.value,
                                            "",
                                            "",
                                            btcHistoryResponse?.data!!.txs!![i].confirmations.toString(),
                                            ""
                                        )
                                    )
                                } else {
                                    if (btcHistoryResponse?.data!!.txs!![i].outgoing != null) {
                                        historylist.add(
                                            TransactionHistory(
                                                btcHistoryResponse?.data!!.txs!![i].txid,
                                                btcHistoryResponse?.data!!.txs!![i].time.toString(),
                                                "Sent",
                                                "8",
                                                "",
                                                "",
                                                btcHistoryResponse?.data!!.txs!![i].outgoing!!.outputs!![0].address,
                                                btcHistoryResponse?.data!!.txs!![i].outgoing!!.value,
                                                btcHistoryResponse?.data!!.txs!![i].outgoing!!.value,
                                                "",
                                                "",
                                                btcHistoryResponse?.data!!.txs!![i].confirmations.toString(),
                                                ""
                                            )
                                        )
                                    } else {
                                        historylist.add(
                                            TransactionHistory(
                                                btcHistoryResponse?.data!!.txs!![i].txid,
                                                btcHistoryResponse?.data!!.txs!![i].time.toString(),
                                                "Received",
                                                "8",
                                                "",
                                                "",
                                                btcHistoryResponse?.data!!.txs!![i].incoming!!.inputs!![0].address,
                                                btcHistoryResponse?.data!!.txs!![i].incoming!!.value,
                                                "",
                                                "",
                                                "",
                                                btcHistoryResponse?.data!!.txs!![i].confirmations.toString(),
                                                ""
                                            )
                                        )
                                    }
                                }

                            }
                            currencyInfoAdapter?.sethistory(
                                historylist,
                                pubadress,
                                coinSymbol,
                                cointype,
                                ethaddress
                            )
                        } else {
                            textLoading?.text = getString(R.string.no_transactions)
                        }
                    } else {
                        textLoading?.text = getString(R.string.no_transactions)
                    }

                    defaultMarket()

                }
            })
        }
    }

    private fun getTransHistory(address: String, contractAddr: String) {
        if (UtilsDefault.isOnline()) {

            if (!isTransHistory) {

                UtilsDefault.updateSharedPreferenceString(Constants.MYADDRESS, address)

                var url = ""

                if (contractAddr != "") {
                    url =
                        "${Constants.API_ETHERSCAN}?module=account&action=tokentx&contractaddress=$contractAddr&address=$address&page=1&offset=100&sort=desc&apikey=${Constants.APIKEY}"
                } else {
                    url =
                        "${Constants.API_ETHERSCAN}?module=account&action=txlist&address=$address&startblock=0&endblock=99999999&page=1&offset=60&sort=desc&apikey=${Constants.APIKEY}"
                }

                nodaWalletAPI.gethistory(url).enqueue(object :
                    Callback<TransactionHistoryResponse> {
                    override fun onFailure(call: Call<TransactionHistoryResponse>, t: Throwable) {
                    }

                    override fun onResponse(
                        call: Call<TransactionHistoryResponse>,
                        response: Response<TransactionHistoryResponse>
                    ) {
                        transactionHistoryResponse = response.body()
                        if (transactionHistoryResponse != null) {
                            if (transactionHistoryResponse?.status == "1") {


                                if (transactionHistoryResponse?.result!!.isNotEmpty()) {
                                    textLoading?.visibility = View.GONE
                                } else {
                                    textLoading?.text = getString(R.string.no_transactions)
                                }

                                val historylist: ArrayList<TransactionHistory> = ArrayList()
                                historylist.clear()

                                for (i in 0..transactionHistoryResponse?.result!!.size.minus(1)) {
                                    val tkn = transactionHistoryResponse?.result!![i].tokenDecimal
                                    var token = ""
                                    if (tkn == null) {
                                        token = ""
                                    } else {
                                        token = transactionHistoryResponse?.result!![i].tokenDecimal
                                    }
                                    historylist.add(
                                        TransactionHistory(
                                            transactionHistoryResponse?.result!![i].hash,
                                            transactionHistoryResponse?.result!![i].timeStamp,
                                            transactionHistoryResponse?.result!![i].nonce,
                                            token,
                                            transactionHistoryResponse?.result!![i].contractAddress
                                            ,
                                            transactionHistoryResponse?.result!![i].from,
                                            transactionHistoryResponse?.result!![i].to,
                                            transactionHistoryResponse?.result!![i].value.toString(),
                                            transactionHistoryResponse?.result!![i].gasPrice,
                                            transactionHistoryResponse?.result!![i].isError,
                                            transactionHistoryResponse?.result!![i].gasUsed!!,
                                            transactionHistoryResponse?.result!![i].confirmations,
                                            transactionHistoryResponse?.result!![i].input
                                        )
                                    )


                                }


                                currencyInfoAdapter?.sethistory(
                                    historylist,
                                    pubadress,
                                    coinSymbol,
                                    cointype,
                                    ethaddress
                                )

                                isTransHistory = true


                            } else {
                                textLoading?.text = getString(R.string.no_transactions)

                            }
                        }

                        defaultMarket()


                    }
                })

            }


        }
    }

    private fun getMarketChart(from: String, to: String) {

        var coin = network

        if (UtilsDefault.isOnline() && isVisible) {
            if (!isChartSet) {

                val currency = UtilsDefault.getSharedPreferenceString(Constants.CURRENCY)
                val symbol = currency?.split("-")!![0]
                nodaWalletAPI.getmarketchart("https://api.coingecko.com/api/v3/coins/$coin/market_chart/range?vs_currency=$symbol&from=$from&to=$to")
                    .enqueue(object : Callback<MarketChartResponse> {
                        override fun onFailure(call: Call<MarketChartResponse>, t: Throwable) {
                        }

                        override fun onResponse(
                            call: Call<MarketChartResponse>,
                            response: Response<MarketChartResponse>
                        ) {
                            if (response.code() == 200) {
                                marketChartResponse = response.body()

                                if (marketChartResponse != null) {
                                    var x = 0
                                    val incvalue = 10
                                    charValue.clear()
                                    xAxis.clear()

                                    for (i in 0..marketChartResponse?.prices?.size!!.minus(1)) {
                                        xAxis.add(marketChartResponse?.prices!![i][0].toString())
                                        charValue.add(
                                            Entry(
                                                x.toFloat(),
                                                marketChartResponse?.prices!![i][1].toFloat()
                                            )
                                        )
                                        x += incvalue
                                    }

                                    if (charValue.size > 0) {
                                        setChartData(charValue)
                                    } else {
                                        chartGraph?.setNoDataText(getString(R.string.no_market_data))
                                        chartGraph?.invalidate()
                                    }

                                }
                            } else {
                                chartGraph?.setNoDataText(getString(R.string.no_market_data))
                                chartGraph?.invalidate()
                            }


                        }

                    })
            }
        }

    }

    private fun setChartData(charValue: java.util.ArrayList<Entry>) {

        try {
            val lineDataSet = LineDataSet(charValue, context?.getString(R.string.market_price))
            lineDataSet.lineWidth = 0.6F
            lineDataSet.color = ContextCompat.getColor(activity!!, R.color.radiobtn)
            lineDataSet.setDrawCircles(false)
            lineDataSet.highLightColor = ContextCompat.getColor(activity!!, R.color.radiobtn)
            lineDataSet.valueTextSize = 10.0F
            lineDataSet.valueTextColor = ContextCompat.getColor(activity!!, R.color.colorPrimaryDark)
            lineDataSet.setDrawFilled(true)
            val draw = ContextCompat.getDrawable(activity!!, R.drawable.chartcolor)
            lineDataSet.fillDrawable = draw
            lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            lineDataSet.setDrawValues(false)
            lineDataSet.setDrawHorizontalHighlightIndicator(false)
            chartGraph.axisLeft.textColor = ContextCompat.getColor(activity!!, R.color.radiobtn)
            chartGraph.xAxis.textColor = ContextCompat.getColor(activity!!, R.color.radiobtn)
            chartGraph.legend.textColor = ContextCompat.getColor(activity!!, R.color.radiobtn)


            val x = chartGraph.xAxis
            x.isEnabled = false
            chartGraph.axisRight.isEnabled = false


            val lineData = LineData(lineDataSet)
            chartGraph.description.text = ""
            chartGraph.description.textSize = 10.0F
            chartGraph.animateY(1000)
            chartGraph.data = lineData
            chartGraph.xAxis.valueFormatter = IndexAxisValueFormatter(xAxis)
            isChartSet = true

            chartGraph.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onNothingSelected() {
                }

                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    val currency = UtilsDefault.getSharedPreferenceString(Constants.CURRENCY)
                    val symbol = currency?.split("-")!![1]
                    textGraphvalue?.text = UtilsDefault.formatToken(e?.y.toString()) + " " + symbol

                }

            })


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onResume() {
        super.onResume()
        mainBack = 1
    }

}