package com.application.nodawallet.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager

import com.application.nodawallet.R
import com.application.nodawallet.activity.MainActivity
import com.application.nodawallet.activity.MainActivity.Companion.mainBack
import com.application.nodawallet.adapter.NewsAdapter
import com.application.nodawallet.application.NodaApplication
import com.application.nodawallet.model.NewsResponse
import com.application.nodawallet.retrofit.NodaWalletAPI
import com.application.nodawallet.utils.UtilsDefault
import kotlinx.android.synthetic.main.activity_faq.*
import kotlinx.android.synthetic.main.fragment_news.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class NewsFragment : BaseFragment(R.layout.fragment_news) {

    @Inject
    lateinit var nodaWalletAPI: NodaWalletAPI

    var newsResponse:NewsResponse?=null
    var newsAdapter:NewsAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NodaApplication.instance.mComponent.inject(this)

        recycleNews.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        newsAdapter = NewsAdapter(activity!!)
        recycleNews.adapter = newsAdapter

        imgBackNews.setOnClickListener {
            (context as MainActivity).push(SettingsFragment())
        }


        getNews()

    }

    private fun getNews() {

        if (UtilsDefault.isOnline()){
            showProgress()

            nodaWalletAPI.getnews().enqueue(object : Callback<NewsResponse>{
                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    hideProgress()

                }

                override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                    hideProgress()

                    newsResponse = response.body()

                    if (newsResponse?.status!!){

                        if (newsResponse?.message!!.size > 0){
                            newsAdapter?.setnews(newsResponse?.message!!)
                        }
                        else {
                            Toast.makeText(activity,"No server response",Toast.LENGTH_SHORT).show()

                        }

                    }
                    else {
                        Toast.makeText(activity,"No server response",Toast.LENGTH_SHORT).show()

                    }
                }

            })



        }
        else {
            Toast.makeText(activity,"No internet",Toast.LENGTH_SHORT).show()
        }

    }

    override fun onResume() {
        super.onResume()
        mainBack = 2
    }


}
