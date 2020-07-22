package com.application.nodawallet.fragment

import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.application.nodawallet.R
import com.application.nodawallet.activity.MainActivity
import com.application.nodawallet.model.CoinList
import com.application.nodawallet.model.MultiCoinList
import com.application.nodawallet.model.MultiWalletList
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.UtilsDefault
import com.application.nodawallet.viewmodel.MultiCoinViewModel
import com.application.nodawallet.viewmodel.MultiWalletViewModel
import kotlinx.android.synthetic.main.fragment_single_coin_address.*
import org.web3j.crypto.WalletUtils


class SingleCoinAddressFragment : BaseFragment(R.layout.fragment_single_coin_address) {

    private lateinit var viewmodel: MultiWalletViewModel
    var size = 0
    private lateinit var viewCoinmodel: MultiCoinViewModel
    var isAddClicked = false
    var address = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel = ViewModelProvider(this).get(MultiWalletViewModel::class.java)
        viewCoinmodel = ViewModelProvider(this).get(MultiCoinViewModel::class.java)

        viewmodel.getAllData.observe(activity!!,
            Observer<List<MultiWalletList>> { t ->
                size = t.size

                if (isAddClicked) {
                    isAddClicked = false
                    insertCoin(t[size.minus(1)].id)
                }

                Log.d("size", size.toString())


            })

        val clipBoardManager =
            context?.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
       

        textSingleAddressPaste.setOnClickListener {
            val text = clipBoardManager.primaryClip?.getItemAt(0)?.text.toString()
            edtAddress.setText(text)
        }

        btnAddressImport.setOnClickListener {

             address = edtAddress.text.toString().trim()

            if (address == "") {
                Toast.makeText(activity, getString(R.string.empty_address), Toast.LENGTH_SHORT).show()
            } else if (!WalletUtils.isValidAddress(address)) {
                Toast.makeText(activity, getString(R.string.invalid_address), Toast.LENGTH_SHORT).show()
            } else {
                val wallet = MultiWalletList(getString(R.string.wallet)+" ${size.plus(1)}", R.drawable.ic_eth, 2, address, "address")
                viewmodel.insert(wallet)
                isAddClicked = true
                UtilsDefault.updateSharedPreferenceInt(Constants.CODE_VERIFY, 1)
                UtilsDefault.updateSharedPreferenceString(Constants.WALLETNAME, getString(R.string.wallet)+" "+size.plus(1))



            }


        }


    }

    private fun insertCoin(id: Int?) {

        var coinlist = ArrayList<CoinList>()

        coinlist.add(CoinList("Ethereum",R.drawable.ic_eth,"https://eth.png","ETH","","public",address,"","",1,"0.000","","","18","ethereum",0.0,0.0,0.0))


        val listcoin = MultiCoinList(id!!, coinlist)
        viewCoinmodel.insert(listcoin)

        UtilsDefault.updateSharedPreferenceInt(Constants.COINID, id)

        if (UtilsDefault.getSharedPreferenceString(Constants.CURRENCY) == null){
            UtilsDefault.updateSharedPreferenceString(Constants.CURRENCY,"USD-$")

        }

        if (UtilsDefault.getSharedPreferenceString(Constants.PUSHNOTIFICATION) == null){
            UtilsDefault.updateSharedPreferenceString(Constants.PUSHNOTIFICATION, "enable")
        }

        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        activity!!.finish()

    }


}
