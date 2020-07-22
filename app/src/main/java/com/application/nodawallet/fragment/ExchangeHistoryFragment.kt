package com.application.nodawallet.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import com.application.nodawallet.R
import com.application.nodawallet.adapter.ExchangeAdapter
import com.application.nodawallet.adapter.ExchangeCoinAdapter
import com.application.nodawallet.application.NodaApplication
import com.application.nodawallet.model.*
import com.application.nodawallet.retrofit.NodaWalletAPI
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.UtilsDefault
import com.application.nodawallet.viewmodel.HistoryViewModel
import com.application.nodawallet.viewmodel.MultiCoinViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_exchange.*
import kotlinx.android.synthetic.main.fragment_exchange_history.*
import kotlinx.android.synthetic.main.fragment_token.*
import okhttp3.Credentials
import okio.Utf8
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class ExchangeHistoryFragment : BaseFragment(R.layout.fragment_exchange_history) {

    @Inject
    lateinit var nodaWalletAPI: NodaWalletAPI
    private lateinit var viewCoinmodel: MultiCoinViewModel
    private var exchangeAdapter: ExchangeAdapter? = null
    val multicoinlist = ArrayList<CoinList>()
    private var exchangeCoinAdapter: ExchangeCoinAdapter? = null

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    var historyUpdate = false
    var templist:ArrayList<CoinList> =  ArrayList()

    var userAddress = ""
    var walletId = 0
    var fromCoin = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NodaApplication.instance.mComponent.inject(this)

        searchHisCoin.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val txt = s.toString()
                filterlist(txt)

            }

        })

        viewCoinmodel = ViewModelProvider(this).get(MultiCoinViewModel::class.java)


        bottomSheetBehavior = BottomSheetBehavior.from<FrameLayout>(bottomsheetExchange)

        recycleExCoin.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        exchangeCoinAdapter = ExchangeCoinAdapter(activity!!)
        recycleExCoin!!.adapter = exchangeCoinAdapter


        recycleExHistory.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        exchangeAdapter = ExchangeAdapter(activity!!)
        recycleExHistory!!.adapter = exchangeAdapter

        llExcSendCoin.setOnClickListener {

            exchangeCoinAdapter?.setCoins(multicoinlist)

            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        exchangeCoinAdapter!!.onItemClick = { pos, view ->
            try {

                var search = searchHisCoin.text.toString().trim()
                var model:CoinList? = null

                model = if (search.isNotEmpty()){
                    templist[pos]

                } else {
                    multicoinlist[pos]
                }

                fromCoin = model.coinSymbol
                textExSendCurrency?.text = fromCoin
                userAddress = model.publicAddress
                if (userAddress!=""){
                  //  getHistory(userAddress)

                }
                searchHisCoin.setText("")

                try {
                    Picasso.with(context).load(model.tokenImage).into(imgExSend)
                }
                catch (e:Exception){
                    e.printStackTrace()
                }

                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        getCoinList()


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

    private fun getCoinList() {
        try {
            viewCoinmodel.getDataById.removeObservers(activity!!)
            viewCoinmodel.getDataById.observe(activity!!,
                Observer<List<MultiCoinList>> { coinlist ->
                    try {
                        if (coinlist.isNotEmpty()) {
                            if (coinlist[0].list.isNotEmpty()) {
                                multicoinlist.clear()
                                for (i in 0..coinlist[0].list.size.minus(1)){
                                    if (coinlist[0].list[i].coinType != "token" && coinlist[0].list[i].coinType == "BEP2") {
                                        if (coinlist[0].list[i].enableStatus == 1){
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

                                        fromCoin = multicoinlist[0].coinSymbol

                                        textExSendCurrency?.text = fromCoin
                                        userAddress = multicoinlist[0].publicAddress

                                        try {
                                            Picasso.with(context).load(multicoinlist[0].tokenImage).into(imgExSend)
                                        }
                                        catch (e:Exception){
                                            e.printStackTrace()
                                        }

                                }
                                }
                            }
                            if (!historyUpdate){
                                historyUpdate = true
                                if (userAddress!=""){
                                    //getHistory(userAddress)
                                }
                            }


                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                })



        } catch (e: Exception) {
            e.printStackTrace()
        }




    }



}
