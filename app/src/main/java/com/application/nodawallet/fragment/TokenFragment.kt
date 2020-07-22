package com.application.nodawallet.fragment

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.nodawallet.R
import com.application.nodawallet.activity.MainActivity
import com.application.nodawallet.activity.WalletConnectActivity
import com.application.nodawallet.adapter.WalletBalanceAdapter
import com.application.nodawallet.application.NodaApplication
import com.application.nodawallet.model.*
import com.application.nodawallet.retrofit.NodaWalletAPI
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.PermissionUtils
import com.application.nodawallet.utils.UtilsDefault
import com.application.nodawallet.viewmodel.MultiCoinViewModel
import com.binance.dex.api.client.BinanceDexApiCallback
import com.binance.dex.api.client.BinanceDexApiClientFactory
import com.binance.dex.api.client.BinanceDexEnvironment
import com.binance.dex.api.client.Wallet
import com.binance.dex.api.client.domain.Account
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_token.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.StringBuilder
import javax.inject.Inject


class TokenFragment : BaseFragment(R.layout.fragment_token) {

    private lateinit var viewCoinmodel: MultiCoinViewModel
    var currencyAvailable = false
    var ethExplorer: ETHExplorer? = null
    val multicoinlist = ArrayList<CoinList>()
    val colist = ArrayList<CoinList>()
    var pricelist = ArrayList<CoinList>()
    var totalMarketResponse: TotalMarketResponse? = null
    var totallist: ArrayList<TotalMarketResponse> = ArrayList()

    var userAddress = ""
    var walletId = 0
    @Inject
    lateinit var nodaWalletAPI: NodaWalletAPI
    var isTokenAdded = false


    var currencyResponse: CurrencyResponse? = null
    var currencylist: ArrayList<NewCurrencyList> = ArrayList()

    private var walletBalanceAdapter: WalletBalanceAdapter? = null

    var PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NodaApplication.instance.mComponent.inject(this)

        recycleWalletBalance.layoutManager =
            LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        walletBalanceAdapter = WalletBalanceAdapter(activity!!)
        recycleWalletBalance!!.adapter = walletBalanceAdapter
        viewCoinmodel = ViewModelProvider(this).get(MultiCoinViewModel::class.java)

        getCoinList()

        swipeToken.setOnRefreshListener {
            isTokenAdded = false
            getCurrencyApi()
            showProgress()
            swipeToken.isRefreshing = false
        }

        imgAdd.setOnClickListener {
            (context as MainActivity).push(AddTokenFragment())
        }

        if (UtilsDefault.getSharedPreferenceString(Constants.WALLETNAME) == null) {
            textMainWallet.text = "Multi-coin-wallet"

        } else {
            textMainWallet.text = "" + UtilsDefault.getSharedPreferenceString(Constants.WALLETNAME)
        }

        imgScan.setOnClickListener {

            val cointype2 = UtilsDefault.getSharedPreferenceString(Constants.COINTYPE)
            if (cointype2 == "public") {
                Toast.makeText(
                    activity,
                    getString(R.string.transaction_publicaddress),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }


            PermissionUtils().permissionList(
                PERMISSIONS,
                activity!!,
                object : PermissionUtils.PermissionCheck {
                    override fun onSuccess(result: String?) {
                        startActivity(Intent(activity, WalletConnectActivity::class.java))
                    }

                })

        }

        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val txt = s.toString()
                filterlist(txt)

            }

        })

    }

    private fun filterlist(txt: String) {

        if (txt != "") {
            var searchtext = txt.toLowerCase()
            var templist: ArrayList<CoinList> = ArrayList()

            for (items in multicoinlist) {
                val coinsy = items.coinName.toLowerCase()
                if (coinsy.startsWith(searchtext)) {
                    templist.add(items)
                }
            }

            walletBalanceAdapter?.setCoins(templist)
        } else {
            walletBalanceAdapter?.setCoins(multicoinlist)
        }

    }


    fun getCoinList() {

        try {
            viewCoinmodel.getDataById.removeObservers(activity!!)
            viewCoinmodel.getDataById.observe(activity!!,
                Observer<List<MultiCoinList>> { coinlist ->
                    try {
                        if (coinlist.isNotEmpty()) {
                            if (coinlist[0].list.isNotEmpty()) {
                                for (i in 0..coinlist[0].list.size.minus(1)) {
                                    if (coinlist[0].list[i].enableStatus == 1) {
                                        currencyAvailable = true
                                        break
                                    }
                                }

                                if (currencyAvailable) {
                                    textNocurrency?.visibility = View.GONE

                                } else {
                                    textNocurrency?.visibility = View.VISIBLE

                                }

                                multicoinlist.clear()
                                multicoinlist.addAll(coinlist[0].list)
                                userAddress = multicoinlist[0].publicAddress
                                walletId = coinlist[0].id
                                UtilsDefault.updateSharedPreferenceString(
                                    Constants.PHRASE,
                                    multicoinlist[0].privateKey
                                )
                                UtilsDefault.updateSharedPreferenceString(
                                    Constants.BNBKEY,
                                    multicoinlist[3].privateKey
                                )
                                UtilsDefault.updateSharedPreferenceString(
                                    Constants.MYADDRESS,
                                    multicoinlist[0].publicAddress
                                )
                                UtilsDefault.updateSharedPreferenceString(
                                    Constants.COINTYPE,
                                    multicoinlist[0].coinType
                                )
                                UtilsDefault.updateSharedPreferenceString(
                                    Constants.SEED,
                                    multicoinlist[0].phrase
                                )


                                var mainbal = 0.0

                                for (i in 0..multicoinlist.size.minus(1)) {
                                    if (multicoinlist[i].enableStatus == 1) {
                                        val bal = multicoinlist[i].balance
                                        if (bal.toDouble() != 0.0) {
                                            val balance = multicoinlist[i].balance.toDouble()
                                            val market = multicoinlist[i].marketPrice.toDouble()
                                            val final = market * balance

                                            mainbal += final
                                        }
                                    }
                                }
                                val currency =
                                    UtilsDefault.getSharedPreferenceString(Constants.CURRENCY)
                                val symbol = currency?.split("-")!![1]
                                if (symbol == "BTC") {
                                    textMainBalance?.text = "" + UtilsDefault.formatCryptoCurrency(mainbal.toString()) + " " + symbol
                                } else {
                                    textMainBalance?.text = "" + UtilsDefault.formatDecimal(mainbal.toString()) + " " + symbol
                                }

                                val set: LinkedHashSet<CoinList> = LinkedHashSet()
                                set.clear()
                                set.addAll(coinlist[0].list)
                                val mylist: ArrayList<CoinList> = ArrayList()
                                mylist.clear()
                                mylist.addAll(set)


                                walletBalanceAdapter?.setCoins(mylist)

                                if (!isTokenAdded) {
                                    isTokenAdded = true
                                    getCurrencyApi()
                                }

                            } else {
                                textNocurrency?.visibility = View.VISIBLE
                            }


                        } else {
                            textNocurrency?.visibility = View.VISIBLE
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                })

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private fun getCurrencyApi() {
        if (UtilsDefault.isOnline()) {
            nodaWalletAPI.getcurrency().enqueue(object : Callback<CurrencyResponse> {
                override fun onFailure(call: Call<CurrencyResponse>, t: Throwable) {
                    try {
                        hideProgress()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onResponse(
                    call: Call<CurrencyResponse>,
                    response: Response<CurrencyResponse>
                ) {
                    try {
                        hideProgress()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    currencyResponse = response.body()

                    if (currencyResponse?.status == 1) {

                        currencylist.clear()

                        for (i in 0..currencyResponse?.data?.size!!.minus(1)) {

                            currencylist.add(
                                NewCurrencyList(
                                    currencyResponse?.data!![i].currency,
                                    currencyResponse?.data!![i].symbol,
                                    currencyResponse?.data!![i].decimal,
                                    currencyResponse?.data!![i].type,
                                    currencyResponse?.data!![i].gekko_id,
                                    currencyResponse?.data!![i].logo,
                                    currencyResponse?.data!![i].contract_address,
                                    currencyResponse?.data!![i].price_change_percentage_24h_usd,
                                    currencyResponse?.data!![i].current_price_usd

                                )
                            )

                        }
                        marketData(currencylist)
                    }
                }
            })
        }
    }

    private fun marketData(currencylist: java.util.ArrayList<NewCurrencyList>) {

        val builder = StringBuilder()

        for (i in 0..currencylist.size.minus(1)) {

            builder.append(",")
            builder.append(currencylist[i].gekko_id)

        }

        val result = builder.toString()
        val passurl = result.substring(1, result.length)

        if (UtilsDefault.isOnline()) {

            val currency = UtilsDefault.getSharedPreferenceString(Constants.CURRENCY)
            val symbol = currency?.split("-")!![0]
            val coingeko =
                "https://api.coingecko.com/api/v3/coins/markets?vs_currency=$symbol&ids=$passurl&order=market_cap_desc"

            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(coingeko).build()

                client.newCall(request).enqueue(object : okhttp3.Callback {
                    override fun onFailure(call: okhttp3.Call, e: IOException) {

                    }

                    override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                        val resp = response.body?.string()
                        try {
                            if (resp != null && resp != "") {
                                val jsonArray = JSONArray(resp)

                                if (jsonArray.length() > 0) {
                                    totallist.clear()

                                    for (i in 0..jsonArray.length().minus(1)) {

                                        val json = jsonArray[i].toString() // your json value here
                                        totalMarketResponse = Gson().fromJson(json, TotalMarketResponse::class.java)
                                        totallist.add(
                                            TotalMarketResponse(
                                                totalMarketResponse?.id,
                                                totalMarketResponse?.symbol,
                                                totalMarketResponse?.name,
                                                totalMarketResponse?.current_price,
                                                totalMarketResponse?.price_change_percentage_24h
                                            )
                                        )
                                    }

                                    updateInLocal(currencylist)


                                }

                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                    }

                })

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


    }

    private fun updateInLocal(currencylist: java.util.ArrayList<NewCurrencyList>) {

        try {
            colist.clear()
            var key = ""
            if (multicoinlist[0].coinType == "public") {
                key = multicoinlist[0].publicAddress
            } else {
                key = multicoinlist[0].privateKey
            }

            var enable = 0

            for (i in 0..multicoinlist.size.minus(1)) {
                colist.add(
                    CoinList(
                        multicoinlist[i].coinName,
                        multicoinlist[i].coinImage,
                        multicoinlist[i].tokenImage,
                        multicoinlist[i].coinSymbol,
                        multicoinlist[i].phrase,
                        multicoinlist[i].coinType,
                        multicoinlist[i].publicAddress,
                        multicoinlist[i].privateKey,
                        multicoinlist[i].publicKey,
                        multicoinlist[i].enableStatus,
                        multicoinlist[i].balance,
                        multicoinlist[i].marketPrice,
                        multicoinlist[i].marketPercentage,
                        multicoinlist[i].decimal,
                        multicoinlist[i].network,
                        multicoinlist[i].minex,
                        multicoinlist[i].maxex,
                        multicoinlist[i].fees
                    )
                )
            }


            if (currencylist.size > 0) {
                for (i in 0..currencylist.size.minus(1)) {
                    enable = if (currencylist[i].symbol == "USDT20") {
                        1
                    } else {
                        0
                    }
                    var isAvailable = false
                    var index = 0
                    for (j in 0..multicoinlist.size.minus(1)) {
                        if (multicoinlist[j].coinSymbol == currencylist[i].symbol) {
                            isAvailable = true
                            index = j
                            break
                        } else {
                            isAvailable = false
                        }
                    }

                    if (!isAvailable) {
                        if (currencylist[i].type == "Token"){
                            colist.add(
                                CoinList(
                                    currencylist[i].currency!!,
                                    R.drawable.ic_round_wallet,
                                    currencylist[i].logo!!,
                                    currencylist[i].symbol!!,
                                    "",
                                    currencylist[i].type!!,
                                    currencylist[i].contract_address!!,
                                    key,
                                    "",
                                    enable,
                                    "0.0",
                                    currencylist[i].currentprice.toString(),
                                    currencylist[i].price24h.toString(),
                                    currencylist[i].decimal.toString(),
                                    currencylist[i].gekko_id!!,
                                    0.00,
                                    0.00,
                                    0.00
                                )
                            )
                        }
                        else if (currencylist[i].type == "BEP2"){
                            val bepkey = UtilsDefault.getSharedPreferenceString(Constants.BNBKEY)
                          val  wallet = Wallet(bepkey, BinanceDexEnvironment.PROD)

                            colist.add(
                                CoinList(
                                    currencylist[i].currency!!,
                                    R.drawable.ic_round_wallet,
                                    currencylist[i].logo!!,
                                    currencylist[i].symbol!!,
                                    "",
                                    currencylist[i].type!!,
                                    wallet.address!!,
                                    bepkey!!,
                                    "",
                                    enable,
                                    "0.0",
                                    currencylist[i].currentprice.toString(),
                                    currencylist[i].price24h.toString(),
                                    currencylist[i].decimal.toString(),
                                    currencylist[i].gekko_id!!,
                                    0.00,
                                    0.00,
                                    0.00
                                )
                            )
                        }


                    }
                }

            }

            val mylist = ArrayList<CoinList>()
            mylist.clear()

            val model = colist

            for (i in 0..colist.size.minus(1)) {

                for (w in 0..totallist.size.minus(1)) {
                    var symbol = ""
                    symbol = if (colist[i].coinSymbol == "USDT20") {
                        "USDT"
                    }
                    else if (colist[i].coinType == "BEP2"){
                        colist[i].coinName.toUpperCase()
                    }
                    else {
                        colist[i].coinSymbol
                    }
                    if (symbol == totallist[w].symbol!!.toUpperCase()) {

                        model[i].marketPrice = totallist[w].current_price.toString()
                        model[i].marketPercentage = totallist[w].price_change_percentage_24h.toString()

                        mylist.add(
                            CoinList(
                                colist[i].coinName,
                                colist[i].coinImage,
                                colist[i].tokenImage,
                                colist[i].coinSymbol,
                                colist[i].phrase,
                                colist[i].coinType,
                                colist[i].publicAddress,
                                colist[i].privateKey,
                                colist[i].publicKey,
                                colist[i].enableStatus,
                                colist[i].balance,
                                colist[i].marketPrice,
                                colist[i].marketPercentage,
                                colist[i].decimal,
                                colist[i].network,
                                colist[i].minex,
                                colist[i].maxex,
                                colist[i].fees
                            )
                        )

                    }
                }
            }

            getBalance(colist)


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getBalance(newList: List<CoinList>) {

        if (UtilsDefault.isOnline()) {

            pricelist.clear()
            pricelist.addAll(newList)

            for (i in 0..newList.size.minus(1)) {
                if (newList[i].enableStatus == 1) {
                    if (newList[i].coinSymbol == "BTC" || newList[i].coinSymbol == "LTC") {
                        getsochain(
                            newList[i].publicAddress,
                            newList[i].coinSymbol
                        )
                    }
                    else if (newList[i].coinSymbol == "BNB"){
                        getBnbBalance(newList[i].publicAddress,newList)
                    }
                    else if (newList[i].coinSymbol == "ETH") {
                        getEthExplorer(newList)
                    }
                }

            }
        }

    }

    private fun getBnbBalance(
        publicAddress: String,
        coinlist: List<CoinList>
    ) {
        val client = BinanceDexApiClientFactory.newInstance().newAsyncRestClient(BinanceDexEnvironment.PROD.baseUrl)
        client.getAccount(publicAddress,object : BinanceDexApiCallback<Account>{
            override fun onResponse(response: Account?) {

                try {
                    if (response!!.balances!!.isNotEmpty()) {
                        for (i in 0..response.balances.size.minus(1)) {

                            for (j in 0..coinlist.size.minus(1)) {

                                if (response.balances[i].symbol!! == coinlist[j].coinSymbol) {
                                    val bal = response.balances[i].free
                                    updateBalance(
                                        bal.toString(),
                                        coinlist[j].coinSymbol,
                                        "",
                                        ""
                                    )
                                }
                            }

                        }

                    }

                } catch (e: Exception) {

                    e.printStackTrace()
                }


            }
            override fun onFailure(cause: Throwable?) {
                super.onFailure(cause)
                updateBalance("0.00", "BNB", "", "")

            }
        })
    }

    private fun getsochain(
        publicAddress: String,
        coinSymbol: String
    ) {

        val passurl = "https://sochain.com/api/v2/address/$coinSymbol/$publicAddress"
        nodaWalletAPI.getbtchistory(passurl).enqueue(object : Callback<BtcHistoryResponse> {
            override fun onFailure(call: Call<BtcHistoryResponse>, t: Throwable) {
                updateBalance("0.00", coinSymbol, "", "")

            }

            override fun onResponse(
                call: Call<BtcHistoryResponse>,
                response: Response<BtcHistoryResponse>
            ) {
                var btcHistoryResponse = response.body()
                if (btcHistoryResponse?.status == "success") {
                    updateBalance(btcHistoryResponse.data!!.balance.toString(), coinSymbol, "", "")
                } else {
                    updateBalance("0.00", coinSymbol, "", "")
                }
            }

        })


    }

    private fun getEthExplorer(coinlist: List<CoinList>) {

        var passurl = "http://api.ethplorer.io/getAddressInfo/$userAddress?apiKey="

        nodaWalletAPI.getethexplorer(passurl).enqueue(object : Callback<ETHExplorer> {
            override fun onFailure(call: Call<ETHExplorer>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ETHExplorer>,
                response: Response<ETHExplorer>
            ) {
                ethExplorer = response.body()
                if (ethExplorer != null) {
                    updateBalance(ethExplorer?.ETH!!.balance.toString(), "ETH", "", "")
                    val tokenlist = ethExplorer?.tokens

                    try {
                        if (tokenlist!!.isNotEmpty()) {
                            for (i in 0..tokenlist.size.minus(1)) {

                                for (j in 0..coinlist.size.minus(1)) {

                                    var symbol = ""
                                    symbol = if (coinlist[j].coinSymbol == "USDT20") {
                                        "USDT"
                                    } else {
                                        coinlist[j].coinSymbol
                                    }


                                    if (tokenlist[i].tokenInfo!!.symbol == symbol) {
                                        val numner = tokenlist[i].tokenInfo!!.decimals
                                        val add = numner.toDouble()
                                        val add2 = add.toInt()
                                        val nu = add2 + 1
                                        val formatted =
                                            String.format("%1$-" + nu + "s", "1").replace(' ', '0')

                                        val bal = ethExplorer?.tokens!![i].balance!! / formatted.toDouble()

                                        updateBalance(
                                            bal.toString(),
                                            coinlist[j].coinSymbol,
                                            "",
                                            ""
                                        )
                                    }
                                }

                            }

                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }

        })

    }

    private fun updateBalance(
        accountbalance: String,
        coinName: String,
        marketprice: String?,
        markepercent: String?
    ) {

        try {

            val mylist = ArrayList<CoinList>()
            mylist.clear()

            val model = pricelist

            for (i in 0..pricelist.size.minus(1)) {

                if (pricelist[i].coinSymbol == coinName) {
                    model[i].balance = accountbalance
                }

                mylist.add(
                    CoinList(
                        pricelist[i].coinName,
                        pricelist[i].coinImage,
                        pricelist[i].tokenImage,
                        pricelist[i].coinSymbol,
                        pricelist[i].phrase,
                        pricelist[i].coinType,
                        pricelist[i].publicAddress,
                        pricelist[i].privateKey,
                        pricelist[i].publicKey,
                        pricelist[i].enableStatus,
                        pricelist[i].balance,
                        pricelist[i].marketPrice,
                        pricelist[i].marketPercentage,
                        pricelist[i].decimal,
                        pricelist[i].network,
                        pricelist[i].minex,
                        pricelist[i].maxex,
                        pricelist[i].fees
                    )
                )
            }

            val updatelist: List<CoinList> = ArrayList<CoinList>(mylist)
            viewCoinmodel.update(updatelist, walletId)
            Log.d("balupdate", "update")
            isTokenAdded = true


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }



}
