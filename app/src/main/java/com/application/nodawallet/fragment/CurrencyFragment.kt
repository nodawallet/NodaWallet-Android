package com.application.nodawallet.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager

import com.application.nodawallet.R
import com.application.nodawallet.activity.MainActivity
import com.application.nodawallet.activity.MainActivity.Companion.mainBack
import com.application.nodawallet.adapter.CurrencyAdapter
import com.application.nodawallet.model.currencyModel
import kotlinx.android.synthetic.main.fragment_currency.*

class CurrencyFragment :BaseFragment(R.layout.fragment_currency) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val list2: ArrayList<currencyModel> = ArrayList()
        list2.clear()

        list2.add(currencyModel("USD","US Dollar","$",R.drawable.ic_usd))
        list2.add(currencyModel("BTC","Bitcoin","BTC",R.drawable.ic_btc))
        list2.add(currencyModel("EUR","Euro","€",R.drawable.ic_eur))
        list2.add(currencyModel("GBP","British Pound","£",R.drawable.ic_gbp))
        list2.add(currencyModel("RUB","Russian Ruble","₽",R.drawable.ic_rub))
        list2.add(currencyModel("AUD","Australian Dollar","A$",R.drawable.ic_aud))
        list2.add(currencyModel("INR","Indian Rupee","₹",R.drawable.ic_inr))
        list2.add(currencyModel("CNY","Chinese yuan","¥",R.drawable.ic_cny))
        list2.add(currencyModel("CAD","Canadian Dollar","C$",R.drawable.ic_cad))
        list2.add(currencyModel("JPY","Japanese Yen","¥",R.drawable.ic_jpy))
        list2.add(currencyModel("KWD","Kuwaiti Dinar","د.ك",R.drawable.ic_kwd))
        list2.add(currencyModel("CHF","Swiss Franc","SFr",R.drawable.ic_chf))


        val linearLayoutManager = LinearLayoutManager(context)
        recyclerview.layoutManager = linearLayoutManager
        val adapter = CurrencyAdapter(context!!, list2)
        recyclerview.adapter = adapter

        imgBackCurrency.setOnClickListener {
            (context as MainActivity).push(SettingsFragment())
        }

    }

    override fun onResume() {
        super.onResume()
        mainBack = 2
    }
}
