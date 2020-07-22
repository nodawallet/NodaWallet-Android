package com.application.nodawallet.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import com.application.nodawallet.R
import com.application.nodawallet.activity.MainActivity
import com.application.nodawallet.adapter.ExportKeysAdapter
import com.application.nodawallet.model.CoinList
import com.application.nodawallet.model.MultiCoinList
import com.application.nodawallet.viewmodel.MultiCoinViewModel
import com.google.android.gms.vision.text.Line
import kotlinx.android.synthetic.main.fragment_public_keys.*


class PublicKeysFragment : BaseFragment(R.layout.fragment_public_keys) {

    private lateinit var viewCoinmodel: MultiCoinViewModel

    val multicoinlist = ArrayList<CoinList>()


    var exportKeysAdapter:ExportKeysAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycleExportKey.layoutManager =  LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        exportKeysAdapter = ExportKeysAdapter(activity!!)
        recycleExportKey.adapter = exportKeysAdapter

        viewCoinmodel = ViewModelProvider(this).get(MultiCoinViewModel::class.java)
        try {
            viewCoinmodel.getDataById.observe(activity!!,
                Observer<List<MultiCoinList>> {
                        coinlist ->

                    if (coinlist.size > 0 && coinlist[0].list.size > 0){

                        multicoinlist.clear()
                            multicoinlist.clear()
                            multicoinlist.addAll(coinlist[0].list)
                            val mylist:ArrayList<CoinList> = ArrayList()
                            mylist.clear()
                            for (i in 0..multicoinlist.size.minus(1)){
                                if (multicoinlist[i].publicKey !=""){
                                    mylist.add(
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
                            }

                            exportKeysAdapter?.setCoins(mylist)

                    }



                })

        }catch (e:Exception){
            e.printStackTrace()
        }



        imgBackExporKey.setOnClickListener {
            (context as MainActivity).push(SettingsFragment())
        }

    }

}
