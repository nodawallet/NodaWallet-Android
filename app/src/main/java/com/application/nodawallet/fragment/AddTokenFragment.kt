package com.application.nodawallet.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.nodawallet.R
import com.application.nodawallet.activity.CustomTokenActivity
import com.application.nodawallet.activity.MainActivity
import com.application.nodawallet.activity.MainActivity.Companion.mainBack
import com.application.nodawallet.adapter.AddTokenAdapter
import com.application.nodawallet.application.NodaApplication
import com.application.nodawallet.model.CoinList
import com.application.nodawallet.model.CurrencyResponse
import com.application.nodawallet.model.GetCurrencyList
import com.application.nodawallet.model.MultiCoinList
import com.application.nodawallet.retrofit.NodaWalletAPI
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.UtilsDefault
import com.application.nodawallet.viewmodel.MultiCoinViewModel

import kotlinx.android.synthetic.main.fragment_add_token.*
import kotlinx.android.synthetic.main.fragment_exchange.*
import kotlinx.android.synthetic.main.fragment_token.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class AddTokenFragment  : BaseFragment(R.layout.fragment_add_token) {

    private lateinit var viewCoinmodel: MultiCoinViewModel
    val multicoinlist = ArrayList<CoinList>()
    var templist:ArrayList<CoinList> =  ArrayList()

    val colist = ArrayList<CoinList>()
    var isTokenAdded = false

    @Inject
    lateinit var nodaWalletAPI: NodaWalletAPI

    var walletId = 0
    private var addTokenAdapter: AddTokenAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NodaApplication.instance.mComponent.inject(this)

        mTokenrecycle.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        addTokenAdapter = AddTokenAdapter(activity!!)
        mTokenrecycle!!.adapter = addTokenAdapter

        viewCoinmodel = ViewModelProvider(this).get(MultiCoinViewModel::class.java)
        try {
            viewCoinmodel.getDataById.observe(activity!!,
                Observer<List<MultiCoinList>> {
                        coinlist ->

                    try {
                        multicoinlist.clear()
                        multicoinlist.addAll(coinlist[0].list)
                         walletId = coinlist[0].id

                        if (coinlist.isNotEmpty() && coinlist[0].list.isNotEmpty()){
                            addTokenAdapter?.setCoins(multicoinlist)
                        }

                    }catch (e:Exception){
                        e.printStackTrace()
                    }



                })

        }catch (e:Exception){
            e.printStackTrace()
        }


        addTokenAdapter!!.onItemClick = {pos, view ->
            try {
                colist.clear()
                val text = edtTokenSearch.text.toString().trim()
                var model:CoinList? = null

                model = if (text.isNotEmpty()){
                    templist[pos]

                } else {
                    multicoinlist[pos]
                }
                val enableStatus: Int

                enableStatus = if (model.enableStatus ==1){
                    0
                } else {
                    1
                }
                model.enableStatus = enableStatus

                for (i in 0..multicoinlist.size.minus(1)){
                    colist.add(CoinList(multicoinlist[i].coinName,multicoinlist[i].coinImage,multicoinlist[i].tokenImage,
                        multicoinlist[i].coinSymbol,multicoinlist[i].phrase,multicoinlist[i].coinType,
                        multicoinlist[i].publicAddress,multicoinlist[i].privateKey,multicoinlist[i].publicKey,
                        multicoinlist[i].enableStatus,multicoinlist[i].balance,multicoinlist[i].marketPrice,
                        multicoinlist[i].marketPercentage,multicoinlist[i].decimal,multicoinlist[i].network,multicoinlist[i].minex,multicoinlist[i].maxex,multicoinlist[i].fees)
                    )
                }

                val updatelist: List<CoinList> = ArrayList<CoinList>(colist)
                viewCoinmodel.update(updatelist, walletId)
                viewCoinmodel.getDataById.removeObservers(activity!!)


            }
            catch (e:Exception){
                e.printStackTrace()
            }
        }


        llAddToken.setOnClickListener {
            startActivity(Intent(activity!!,CustomTokenActivity::class.java))
        }

        imgBackToken.setOnClickListener {
            (context as MainActivity).push(HomeFragment())
        }

        edtTokenSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val txt = s.toString()
                filterlist(txt)

            }

        })

        textHeader.text = getString(R.string.add_token)
    }

    private fun filterlist(txt: String) {

        if (txt !=""){
            templist.clear()
            val searchtext = txt.toLowerCase()
            for (items in multicoinlist){
                val coinsy = items.coinName.toLowerCase()
                if (coinsy.startsWith(searchtext)){
                    templist.add(items)
                }
            }
            addTokenAdapter?.setCoins(templist)
        }
        else {
            addTokenAdapter?.setCoins(multicoinlist)
        }

    }

    override fun onResume() {
        super.onResume()

        mainBack = 1

    }
}
