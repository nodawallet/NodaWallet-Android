package com.application.nodawallet.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager

import com.application.nodawallet.R
import com.application.nodawallet.activity.MainActivity
import com.application.nodawallet.adapter.NewsAdapter
import com.application.nodawallet.application.NodaApplication
import com.application.nodawallet.model.CoinList
import com.application.nodawallet.model.NewsList
import com.application.nodawallet.model.NewsResponse
import com.application.nodawallet.retrofit.NodaWalletAPI
import com.application.nodawallet.utils.UtilsDefault
import kotlinx.android.synthetic.main.fragment_collectibles.*
import kotlinx.android.synthetic.main.fragment_news.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class InvestmentFragment : BaseFragment(R.layout.fragment_collectibles) {

    @Inject
    lateinit var nodaWalletAPI: NodaWalletAPI
    var newsResponse: NewsResponse?=null
    var newsAdapter:NewsAdapter? = null
    var newslist:ArrayList<NewsList> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NodaApplication.instance.mComponent.inject(this)

        recycleInvest.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        newsAdapter = NewsAdapter(activity!!)
        recycleInvest.adapter = newsAdapter

        edtSearchNews.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val txt = s.toString()
                filterlist(txt)
            }

        })

        btnRefresh.setOnClickListener {
            getNews()
        }

        swipeInvestments.setOnRefreshListener {
            newslist.clear()
            newsAdapter?.setnews(newslist)
            getNews()
            swipeInvestments.isRefreshing = false
        }

        getNews()

    }
    private fun filterlist(txt: String) {

        if (txt !=""){
            val searchtext = txt.toLowerCase()
            val templist:ArrayList<NewsList> =  ArrayList()
            for (items in newslist){
                val coinsy = items.heading?.toLowerCase()
                if (coinsy!!.contains(searchtext)){
                    templist.add(items)
                }
            }

            newsAdapter?.setnews(templist)
        }
        else {
            newsAdapter?.setnews(newslist.reversed())

        }
    }

    private fun getNews() {

        if (UtilsDefault.isOnline()){

            nodaWalletAPI.getnews().enqueue(object : Callback<NewsResponse> {
                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    consInvest?.visibility =  View.VISIBLE

                }

                override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {

                    newsResponse = response.body()

                    try {
                        if (newsResponse?.status!!){

                            if (newsResponse?.message!!.isNotEmpty()){
                                consInvest?.visibility =  View.GONE

                                newslist.clear()
                                newslist.addAll(newsResponse?.message!!)
                                if (newslist.size > 0){
                                    newsAdapter?.setnews(newslist.reversed())
                                }
                                else {
                                    consInvest?.visibility =  View.VISIBLE
                                }
                            }
                            else {
                                consInvest?.visibility =  View.VISIBLE
                            }
                        }
                        else {
                            consInvest?.visibility =  View.VISIBLE
                        }
                    }
                    catch (e:Exception){
                        e.printStackTrace()
                    }
                }

            })

        }
        else {
            consInvest?.visibility =  View.VISIBLE
            Toast.makeText(activity,getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }

    }




}
