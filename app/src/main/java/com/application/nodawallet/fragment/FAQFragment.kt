package com.application.nodawallet.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.nodawallet.R
import com.application.nodawallet.activity.CustomTokenActivity
import com.application.nodawallet.activity.MainActivity
import com.application.nodawallet.activity.MainActivity.Companion.mainBack
import com.application.nodawallet.adapter.AddTokenAdapter
import com.application.nodawallet.adapter.FAQAdapter
import com.application.nodawallet.application.NodaApplication
import com.application.nodawallet.model.*
import com.application.nodawallet.retrofit.NodaWalletAPI
import com.application.nodawallet.utils.UtilsDefault
import com.application.nodawallet.viewmodel.MultiCoinViewModel
import kotlinx.android.synthetic.main.activity_faq.*
import kotlinx.android.synthetic.main.activity_faq.textHeader
import kotlinx.android.synthetic.main.fragment_currency.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class FAQFragment : BaseFragment(R.layout.activity_faq) {

    var mFaqAdapter: FAQAdapter? = null
    var faqResponse:FAQResponse? =null

    @Inject
    lateinit var nodaWalletAPI: NodaWalletAPI



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NodaApplication.instance.mComponent.inject(this)

        getFAQ()

        imgBackFaq.setOnClickListener {
            (context as MainActivity).push(SettingsFragment())
        }


       // mGetcms()
        textHeader.text = getString(R.string.faq)

       /* imgBackCurrency.setOnClickListener {
            (context as MainActivity).push(SettingsFragment())
        }*/
    }

    private fun getFAQ() {
        if (UtilsDefault.isOnline()){

            showProgress()

            nodaWalletAPI.getFaq().enqueue(object : Callback<FAQResponse> {
                override fun onFailure(call: Call<FAQResponse>, t: Throwable) {
                    hideProgress()
                }

                override fun onResponse(
                    call: Call<FAQResponse>,
                    response: Response<FAQResponse>
                ) {
                    hideProgress()

                    faqResponse = response.body()
                    if (faqResponse?.status!!){

                        var list:ArrayList<FaqList> = ArrayList()
                        list.clear()

                        list.addAll(faqResponse?.message!!)

                        if (list.size > 0) {
                            mFound?.visibility = View.GONE
                            faq_Recycle.layoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
                            mFaqAdapter = FAQAdapter(list, activity!!, 1)
                            faq_Recycle.adapter = mFaqAdapter

                        } else {
                            mFound?.visibility = View.VISIBLE
                        }
                    }

                }

            })


        }
        else{
            Toast.makeText(activity,"No internet",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        mainBack = 2

    }
}