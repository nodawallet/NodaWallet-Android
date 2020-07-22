package com.application.nodawallet.fragment

import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import kotlinx.android.synthetic.main.fragment_single_coin_phrase.*
import org.web3j.crypto.Bip32ECKeyPair
import org.web3j.crypto.Credentials
import org.web3j.crypto.MnemonicUtils
import org.web3j.utils.Numeric


class SingleCoinPhraseFragment : BaseFragment(R.layout.fragment_single_coin_phrase) {

    private lateinit var viewmodel: MultiWalletViewModel
    private lateinit var viewCoinmodel: MultiCoinViewModel
    var isAddClicked = false

    var phrase = ""
    var size = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewmodel = ViewModelProvider(this).get(MultiWalletViewModel::class.java)
        viewCoinmodel = ViewModelProvider(this).get(MultiCoinViewModel::class.java)

        viewmodel.getAllData.observe(activity!!,
            Observer<List<MultiWalletList>> { t ->
                size = t.size
                if (isAddClicked){
                    isAddClicked = false
                    insertCoin(t[size.minus(1)].id)
                }


                Log.d("size",size.toString())


            })

        val clipBoardManager = context?.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManage

        textSinglePhrasePaste.setOnClickListener {
            val text = clipBoardManager.primaryClip?.getItemAt(0)?.text.toString()
            edtSinglePhrase.setText(text)
        }

        btnSingleImport.setOnClickListener {

             phrase = edtSinglePhrase.text.toString().trim()

            if (phrase == "") {
                Toast.makeText(activity, getString(R.string.empty_phrase), Toast.LENGTH_SHORT).show()
            } else if (!MnemonicUtils.validateMnemonic(phrase)) {
                Toast.makeText(activity, getString(R.string.invalid_phrase), Toast.LENGTH_SHORT).show()
            } else {
                val wallet = MultiWalletList(getString(R.string.wallet)+" ${size.plus(1)}",  R.drawable.ic_eth,1, UtilsDefault.mnemonicToPrivateKey(phrase),"phrase")
                viewmodel.insert(wallet)
                isAddClicked = true
                UtilsDefault.updateSharedPreferenceInt(Constants.CODE_VERIFY, 1)
                UtilsDefault.updateSharedPreferenceString(Constants.WALLETNAME, getString(R.string.wallet)+" "+size.plus(1))

            }


        }

    }
    private fun insertCoin(id:Int?) {

        var coinlist =  ArrayList<CoinList>()


        val mnemonic = phrase
        val derivationPath = intArrayOf(44 or Bip32ECKeyPair.HARDENED_BIT, 60 or Bip32ECKeyPair.HARDENED_BIT, 0 or Bip32ECKeyPair.HARDENED_BIT, 0,0)
        val masterkeypair =  Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(mnemonic,""))
        val derivedKeyPair =  Bip32ECKeyPair.deriveKeyPair(masterkeypair,derivationPath)

        val credentials =  Credentials.create(derivedKeyPair)



        coinlist.add(CoinList("Ethereum",R.drawable.ic_eth,"eth.png","ETH",mnemonic,"Coin",credentials.address,
            Numeric.toHexStringNoPrefix(credentials.ecKeyPair.privateKey),credentials.ecKeyPair.publicKey.toString(),1,"0.000","","","18","ethereum",0.0,0.0,0.0))


        val listcoin = MultiCoinList(id!!,coinlist)
        viewCoinmodel.insert(listcoin)
        UtilsDefault.updateSharedPreferenceInt(Constants.COINID,id)
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
