package com.application.nodawallet.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.application.nodawallet.R
import com.application.nodawallet.activity.MainActivity
import com.application.nodawallet.activity.MainActivity.Companion.mainBack
import com.application.nodawallet.model.CoinList
import com.application.nodawallet.model.MultiCoinList
import com.application.nodawallet.model.MultiWalletList
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.UtilsDefault
import com.application.nodawallet.viewmodel.MultiCoinViewModel
import com.application.nodawallet.viewmodel.MultiWalletViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_advanced_settings.*
import kotlinx.android.synthetic.main.fragment_currency_detail.*
import kotlinx.android.synthetic.main.fragment_token.*
import kotlinx.android.synthetic.main.item_main_allwallet.*
import org.web3j.crypto.Bip32ECKeyPair
import org.web3j.crypto.Credentials
import org.web3j.crypto.MnemonicUtils
import org.web3j.utils.Numeric


class AdvancedSettingsFragment : BaseFragment(R.layout.fragment_advanced_settings) {

    private lateinit var viewmodel: MultiWalletViewModel

    val multicoinlist = ArrayList<CoinList>()
    var phrase = ""
    var walletId = 0
    var walletname = ""
    var walletimage = 0
    var walletType = 0
    var importType = ""
    var cointype = ""
    var address = ""

    var list = ArrayList<MultiWalletList>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        if (bundle !=null){
            walletId =  bundle.getInt("walletId")
        }

        if (UtilsDefault.getSharedPreferenceInt(Constants.COINID) == walletId){
            imgDelete.visibility = View.GONE
        }

        viewmodel = ViewModelProvider(activity!!).get(MultiWalletViewModel::class.java)



        viewmodel.getAllData.observe(activity!!,
            Observer<List<MultiWalletList>> { wallet ->

                for (i in 0..wallet.size.minus(1)){

                    if (wallet[i].id == walletId){

                        walletname = wallet[i].walletName
                        walletimage = wallet[i].walletImage
                        phrase = wallet[i].phrase
                        walletType = wallet[i].walletType
                        importType = wallet[i].importType

                    }
                }

                edtWalletName?.setText(""+walletname)
                mainwalletName?.text = ""+walletname
                if (walletimage!=null){
                    wallet_image?.setImageResource(walletimage)

                }

                if (importType == "privatekey"){
                    llShowPhrase?.visibility = View.GONE
                    llExportPublickey?.visibility = View.GONE
                    llShowPrivate?.visibility = View.VISIBLE
                    llCopyAddress?.visibility = View.VISIBLE

                    val credential = Credentials.create(phrase)
                     address = credential.address


                }
                else if (importType == "phrase"){
                    llShowPhrase?.visibility = View.VISIBLE
                    llShowPrivate?.visibility = View.GONE
                    llExportPublickey?.visibility = View.GONE
                    llCopyAddress?.visibility = View.VISIBLE

                    val mnemonic = phrase
                    val derivationPath = intArrayOf(44 or Bip32ECKeyPair.HARDENED_BIT, 60 or Bip32ECKeyPair.HARDENED_BIT, 0 or Bip32ECKeyPair.HARDENED_BIT, 0,0)
                    val masterkeypair =  Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(mnemonic,""))
                    val derivedKeyPair =  Bip32ECKeyPair.deriveKeyPair(masterkeypair,derivationPath)

                    val credentials =  Credentials.create(derivedKeyPair)
                    address = credentials.address



                }
                else if (importType == "address"){
                    llShowPhrase?.visibility = View.GONE
                    llShowPrivate?.visibility = View.GONE
                    llExportPublickey?.visibility = View.GONE
                    llCopyAddress?.visibility = View.VISIBLE

                    address = phrase

                }
                else if (importType == "multi"){
                    llShowPhrase?.visibility = View.VISIBLE
                    llExportPublickey?.visibility = View.VISIBLE
                    llShowPrivate?.visibility = View.GONE
                    llCopyAddress?.visibility = View.GONE

                    val mnemonic = phrase
                    val derivationPath = intArrayOf(44 or Bip32ECKeyPair.HARDENED_BIT, 60 or Bip32ECKeyPair.HARDENED_BIT, 0 or Bip32ECKeyPair.HARDENED_BIT, 0,0)
                    val masterkeypair =  Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(mnemonic,""))
                    val derivedKeyPair =  Bip32ECKeyPair.deriveKeyPair(masterkeypair,derivationPath)

                    val credentials =  Credentials.create(derivedKeyPair)
                    address = credentials.address

                }

            })

        textCopyAddress?.text = address




        imgBackAdvanced.setOnClickListener {
            (context as MainActivity).push(MainWalletFragment())
        }

        imgTick.setOnClickListener {
            updateName()
        }

        imgDelete.setOnClickListener {
            deleteWallet()
        }

        consSecretPhase.setOnClickListener {

            val fragment =  ShowRecoveryFragment()
            val bundle = Bundle()
            bundle.putString("phrase",phrase)
            fragment.arguments = bundle
            (context as MainActivity).push(fragment)
        }

        consCopyAddress.setOnClickListener {
            UtilsDefault.copyToClipboard(activity!!,address)
            Toast.makeText(activity,getString(R.string.address_copied),Toast.LENGTH_SHORT).show()

        }

        consPrivateKey.setOnClickListener {
            val fragment =  PrivateKeyExportFragment()
            val bundle = Bundle()
            bundle.putString("phrase",phrase)
            fragment.arguments = bundle
            (context as MainActivity).push(fragment)
        }

        consExportKey.setOnClickListener {
            (context as MainActivity).push(PublicKeysFragment())
        }

        edtWalletName.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                imgTick.visibility = View.VISIBLE

            }

        })
    }

    private fun deleteWallet() {
        MaterialAlertDialogBuilder(activity,R.style.MyAlertDialog).apply {
            setMessage(getString(R.string.delete_wallet))
            setPositiveButton(getString(R.string.yes)) { dialogInterface: DialogInterface, i: Int ->

                try {

                    viewmodel.deletItem(walletId)
                    dialogInterface.dismiss()

                    Toast.makeText(activity,getString(R.string.wallet_deleted),Toast.LENGTH_SHORT).show()

                    val fm = activity?.supportFragmentManager
                    val ft = fm?.beginTransaction()
                    ft!!.replace(R.id.mainFrameContainer,MainWalletFragment())
                    ft.commit()
                }catch (e:Exception){
                    e.printStackTrace()
                }

            }
            setNegativeButton(getString(R.string.no)) { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
            }
        }.show()

    }

    fun updateName(){
        val walletname = edtWalletName.text.toString().trim()
        if (walletname == "" || walletname.length < 3){
            Toast.makeText(activity,getString(R.string.wallet_name_validation),Toast.LENGTH_SHORT).show()
        }
        else {
            viewmodel.update(walletname,walletId)
            UtilsDefault.updateSharedPreferenceString(Constants.WALLETNAME,walletname)

            imgTick.visibility = View.GONE
            Toast.makeText(activity,getString(R.string.name_updated),Toast.LENGTH_SHORT).show()

        }

    }

    override fun onResume() {
        super.onResume()
        mainBack = 3
    }

}
