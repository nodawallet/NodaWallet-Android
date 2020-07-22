package com.application.nodawallet.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import com.application.nodawallet.R
import com.application.nodawallet.activity.MainActivity
import com.application.nodawallet.activity.MainActivity.Companion.mainBack
import com.application.nodawallet.activity.PatternActivity
import com.application.nodawallet.adapter.MainAllWalletAdapter
import com.application.nodawallet.model.AllWalletList
import com.application.nodawallet.model.MainAdapterList
import com.application.nodawallet.model.MultiList
import com.application.nodawallet.model.MultiWalletList
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.UtilsDefault
import com.application.nodawallet.viewmodel.MultiWalletViewModel
import kotlinx.android.synthetic.main.fragment_main_wallet.*

class MainWalletFragment : BaseFragment(R.layout.fragment_main_wallet) {

     var mainallWalletAdapter: MainAllWalletAdapter? = null
    var list = ArrayList<AllWalletList>()
    var walletlist:ArrayList<MainAdapterList> = ArrayList()


    private lateinit var viewmodel: MultiWalletViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgBackMain.setOnClickListener {
            (context as MainActivity).push(SettingsFragment())
        }

        imgAdd.setOnClickListener {
            val intent = Intent(activity, PatternActivity::class.java)
            startActivity(intent)
        }

        recycleMainWallet.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        mainallWalletAdapter = MainAllWalletAdapter(activity!!,walletlist)
        recycleMainWallet!!.adapter = mainallWalletAdapter

        viewmodel = ViewModelProvider(activity!!).get(MultiWalletViewModel::class.java)
        viewmodel.getAllData.observe(activity!!,
            Observer<List<MultiWalletList>> { wallet ->

                val wall0 = ArrayList<MultiList>()
                val wall1 = ArrayList<MultiList>()
                val wall2 = ArrayList<MultiList>()

                walletlist.clear()
                wall0.clear()
                wall1.clear()
                wall2.clear()

                for (i in 0..wallet.size.minus(1)) {
                    if (wallet[i].walletType == 0) {
                        wall0.add(
                            MultiList(
                                wallet[i].id,
                                wallet[i].walletName,
                                wallet[i].walletImage,
                                wallet[i].walletType,
                                wallet[i].phrase,
                                wallet[i].importType

                            )
                        )
                    } else if (wallet[i].walletType == 1) {
                        wall1.add(
                            MultiList(
                                wallet[i].id,
                                wallet[i].walletName,
                                wallet[i].walletImage,
                                wallet[i].walletType,
                                wallet[i].phrase,
                                wallet[i].importType
                            )
                        )

                    } else if (wallet[i].walletType == 2) {
                        wall2.add(
                            MultiList(
                                wallet[i].id,
                                wallet[i].walletName,
                                wallet[i].walletImage,
                                wallet[i].walletType,
                                wallet[i].phrase,
                                wallet[i].importType
                            )
                        )

                    }
                }


                walletlist.clear()

                if (wall0.size > 0){
                    walletlist.add(MainAdapterList(null,true,activity?.getString(R.string.multicoin_wallet)))
                    for (i in 0..wall0.size.minus(1)){
                        walletlist.add(MainAdapterList(MultiList(wall0[i].walletId,wall0[i].walletName,wall0[i].walletImage,wall0[i].walletType,
                            wall0[i].phrase,wall0[i].importType),false,null))

                    }

                }

                if (wall1.size > 0){
                    walletlist.add(MainAdapterList(null,true,activity?.getString(R.string.wallets)))
                    for (i in 0..wall1.size.minus(1)){
                        walletlist.add(MainAdapterList(MultiList(wall1[i].walletId,wall1[i].walletName,wall1[i].walletImage,wall1[i].walletType,
                            wall1[i].phrase,wall1[i].importType),false,null))

                    }

                }

                if (wall2.size > 0){
                    walletlist.add(MainAdapterList(null,true,activity?.getString(R.string.address)))
                    for (i in 0..wall2.size.minus(1)){
                        walletlist.add(MainAdapterList(MultiList(wall2[i].walletId,wall2[i].walletName,wall2[i].walletImage,wall2[i].walletType,
                            wall2[i].phrase,wall2[i].importType),false,null))

                    }

                }

                mainallWalletAdapter?.setWallets(walletlist)


            })


    }

    override fun onResume() {
        super.onResume()

        mainBack = 2

    }

}
