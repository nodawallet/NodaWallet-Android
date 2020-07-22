package com.application.nodawallet.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.application.nodawallet.R
import com.application.nodawallet.activity.MainActivity
import com.application.nodawallet.activity.PatternActivity
import com.application.nodawallet.activity.PatternActivity.Companion.patternBack
import com.application.nodawallet.adapter.PatternAdapter
import com.application.nodawallet.adapter.PatternVerifyAdapter
import com.application.nodawallet.application.NodaApplication
import com.application.nodawallet.model.*
import com.application.nodawallet.retrofit.NodaWalletAPI
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.UtilsDefault
import com.application.nodawallet.viewmodel.MultiCoinViewModel
import com.application.nodawallet.viewmodel.MultiWalletViewModel
import com.binance.dex.api.client.BinanceDexEnvironment
import com.binance.dex.api.client.Wallet
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.android.synthetic.main.activity_pattern.*
import kotlinx.android.synthetic.main.fragment_verify_pattern.*
import org.bitcoinj.core.LegacyAddress
import org.bitcoinj.crypto.DeterministicKey
import org.bitcoinj.crypto.HDKeyDerivation
import org.bitcoinj.crypto.HDUtils
import org.bitcoinj.crypto.MnemonicCode
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.wallet.DeterministicKeyChain
import org.bitcoinj.wallet.DeterministicSeed
import org.web3j.crypto.Bip32ECKeyPair
import org.web3j.crypto.Credentials
import org.web3j.crypto.MnemonicUtils
import org.web3j.utils.Numeric
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.inject.Inject

class VerifyPatternFragment : BaseFragment(R.layout.fragment_verify_pattern) {

    private var patternAdapter: PatternAdapter? = null
    private var patternVerifyAdapter: PatternVerifyAdapter? = null

    var list = arrayListOf<String>()
    var listTemp = arrayListOf<String>()
    var listverify = arrayListOf<String>()
    var isCodeVerified = false
    var size = 0
    var isAddClicked = false
    var phrase = ""
    val coinlist =  ArrayList<CoinList>()
    var balanceResponse:BalanceResponse?=null
    var notifyList = ArrayList<CurrencyNofify>()



    @Inject
    lateinit var nodaWalletAPI: NodaWalletAPI


    private lateinit var viewmodel: MultiWalletViewModel
    private lateinit var viewCoinmodel: MultiCoinViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NodaApplication.instance.mComponent.inject(this)


        val bundle = arguments
        if (bundle!=null){
            phrase = bundle.getString("phrase") ?: ""
        }

        val lstValues: List<String> = phrase.split(" ").map { it -> it.trim() }
        list.clear()
        list.addAll(lstValues)

        listTemp.addAll(list)

        Collections.rotate(list, 3)
        list.shuffle(Random())
        list.shuffle()


        val layoutManager = FlexboxLayoutManager(activity)
        layoutManager.justifyContent = JustifyContent.CENTER
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.flexWrap = FlexWrap.WRAP
        recycleConfirm.layoutManager = layoutManager

        patternAdapter = PatternAdapter(activity!!, list)
        recycleConfirm.adapter = patternAdapter

        patternAdapter!!.onItemClick = { pos, _ ->

            setVerifyPhrase(pos)
        }


        val layoutManager1 = FlexboxLayoutManager(activity)
        layoutManager1.justifyContent = JustifyContent.CENTER
        layoutManager1.flexDirection = FlexDirection.ROW
        layoutManager1.flexWrap = FlexWrap.WRAP
        recycleVerify.layoutManager = layoutManager1


        patternVerifyAdapter = PatternVerifyAdapter(activity!!, listverify)
        recycleVerify.adapter = patternVerifyAdapter

        patternVerifyAdapter?.onItemClick = { pos, _ ->
            deletePhrase(pos)
        }


        viewmodel = ViewModelProvider(activity!!).get(MultiWalletViewModel::class.java)
        viewCoinmodel = ViewModelProvider(this).get(MultiCoinViewModel::class.java)

        viewmodel.getAllData.observe(activity!!,
            Observer<List<MultiWalletList>> { t ->
                size = t.size

                try {
                    if (isAddClicked){
                        isAddClicked = false
                        insertCoin(t[size.minus(1)].id)
                    }
                }
                catch (e:ArrayIndexOutOfBoundsException){
                    if (isAddClicked){
                        isAddClicked = false
                        insertCoin(t[size].id)
                    }
                    e.printStackTrace()
                }

            })

        btnDone.setOnClickListener {

            if (isCodeVerified) {

                val wallet = MultiWalletList(getString(R.string.multi_coin_wallet)+size.plus(1), R.drawable.ic_round_wallet, 0, phrase,"multi")
                viewmodel.insert(wallet)
                UtilsDefault.updateSharedPreferenceInt(Constants.CODE_VERIFY, 1)
                UtilsDefault.updateSharedPreferenceString(Constants.WALLETNAME, getString(R.string.multi_coin_wallet)+size.plus(1))

                isAddClicked = true
            }

        }

    }

    private fun insertCoin(id: Int) {
        coinlist.clear()
        AddETH(id)
    }

    private fun AddBTC(id: Int?) {

        val params = MainNetParams.get()
        val time = System.currentTimeMillis()/1000

        val mnemonic = phrase
        val seede = DeterministicSeed(mnemonic,null,"",time)

        val chain = DeterministicKeyChain.builder().seed(seede).build()
        val keypath = HDUtils.parsePath("M/44H/0H/0H/0/0") //mainnet
        val key = chain.getKeyByPath(keypath,true)
        val privateKey = key.getPrivateKeyEncoded(params)
        val publicKey  = key.publicKeyAsHex
        val address = LegacyAddress.fromKey(params,key).toBase58()



        coinlist.add(CoinList("Bitcoin",R.drawable.ic_btc,"btc.png","BTC",mnemonic,
            "Coin",address,privateKey.toString(),
            publicKey.toString(),1,"0.000","0.00",
            "0.00","8","bitcoin",0.0,0.0,0.0))

        notifyList.add(CurrencyNofify(address,"BTC"))
        AddLTC(id)



    }

    private fun AddLTC(id: Int?) {


        val params = LitecoinMainNetParams.get()
        val time = System.currentTimeMillis()/1000

        val mnemonic = phrase
        val seede = DeterministicSeed(mnemonic,null,"",time)

        val chain = DeterministicKeyChain.builder().seed(seede).build()
        val keypath = HDUtils.parsePath("M/44H/2H/0H/0/0") //mainnet
        val key = chain.getKeyByPath(keypath,true)
        val privateKey = key.getPrivateKeyEncoded(params)
        val publicKey  = key.publicKeyAsHex
        val address = LegacyAddress.fromKey(params,key).toBase58()



        coinlist.add(CoinList("Litecoin",R.drawable.ic_ltc,"ltc.png","LTC",mnemonic,
            "Coin",address,privateKey.toString(),
            publicKey.toString(),1,"0.000","0.00",
            "0.00","8","litecoin",0.0,0.0,0.0))

        val updatelist: List<CoinList> = ArrayList<CoinList>(coinlist)
        val listcoin = MultiCoinList(id!!,updatelist)
        viewCoinmodel.insert(listcoin)

        UtilsDefault.updateSharedPreferenceInt(Constants.COINID,id)

        if (UtilsDefault.getSharedPreferenceString(Constants.CURRENCY) == null){
            UtilsDefault.updateSharedPreferenceString(Constants.CURRENCY,"USD-$")

        }

        if (UtilsDefault.getSharedPreferenceString(Constants.PUSHNOTIFICATION) == null){
            UtilsDefault.updateSharedPreferenceString(Constants.PUSHNOTIFICATION, "enable")
        }

        notifyList.add(CurrencyNofify(address,"LTC"))
        AddBNB(id)



    }

    private fun AddETH(id: Int?) {

        showProgress()

        val mnemonic = phrase
        val derivationPath = intArrayOf(44 or Bip32ECKeyPair.HARDENED_BIT, 60 or Bip32ECKeyPair.HARDENED_BIT, 0 or Bip32ECKeyPair.HARDENED_BIT, 0,0)
        val masterkeypair =  Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(mnemonic,""))
        val derivedKeyPair =  Bip32ECKeyPair.deriveKeyPair(masterkeypair,derivationPath)

        val credentials =  Credentials.create(derivedKeyPair)


        coinlist.add(CoinList("Ethereum",R.drawable.ic_eth,"eth.png","ETH",mnemonic,
            "Coin",credentials.address,
            Numeric.toHexStringNoPrefix(credentials.ecKeyPair.privateKey), credentials.ecKeyPair.publicKey.toString(),
            1,"0.000","0.00",
            "0.00","18","ethereum",0.0,0.0,0.0))

        notifyList.clear()
        notifyList.add(CurrencyNofify(credentials?.address,"ETH"))
        AddBTC(id)

    }

    private fun AddBNB(id: Int?) {
        val mnemonic = phrase
        val lstValues: List<String> = mnemonic.split(" ").map { it -> it.trim() }
        val privatekey = getPrivateKeyFromMnemonicCode(lstValues)
        val wallet = Wallet(privatekey, BinanceDexEnvironment.PROD)

        coinlist.add(CoinList("Binance",R.drawable.binance,"https://bnb.png","BNB",mnemonic,
            "Coin",wallet.address,privatekey.toString(),
            Numeric.toHexStringNoPrefix(wallet.pubKeyForSign),1,"0.000","0.00",
            "0.00","8","binancecoin",0.0,0.0,0.0))

        val updatelist: List<CoinList> = ArrayList<CoinList>(coinlist)
        val listcoin = MultiCoinList(id!!,updatelist)
        viewCoinmodel.insert(listcoin)

        notifyList.add(CurrencyNofify(wallet.address,"BNB"))


        UtilsDefault.updateSharedPreferenceInt(Constants.COINID,id)

        if (UtilsDefault.getSharedPreferenceString(Constants.CURRENCY) == null){
            UtilsDefault.updateSharedPreferenceString(Constants.CURRENCY,"USD-$")

        }

        if (UtilsDefault.getSharedPreferenceString(Constants.PUSHNOTIFICATION) == null){
            UtilsDefault.updateSharedPreferenceString(Constants.PUSHNOTIFICATION, "enable")
        }

        hideProgress()
        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()

    }


    private fun deletePhrase(pos: Int) {

        list.add(listverify[pos])
        listverify.remove(listverify[pos])
        patternAdapter?.deleteItem(list)
        patternVerifyAdapter?.deleteItem(listverify)

        if (listverify.size == 12) {
            verifySeed()
        } else {
            btnDone.alpha = 0.5F
            isCodeVerified = false
            UtilsDefault.updateSharedPreferenceInt(Constants.CODE_VERIFY, 0)
            textCodeVerify.text = ""
        }

    }

    private fun setVerifyPhrase(pos: Int) {

        listverify.add(list[pos])
        list.remove(list[pos])
        patternAdapter?.deleteItem(list)
        patternVerifyAdapter?.deleteItem(listverify)


        if (listverify.size == 12) {
            verifySeed()
        } else {
            btnDone.alpha = 0.5F
            isCodeVerified = false
            UtilsDefault.updateSharedPreferenceInt(Constants.CODE_VERIFY, 0)
            textCodeVerify.text = ""
        }


    }

    infix fun <T> Collection<T>.deepEqualTo(other: Collection<T>): Boolean {
        if (this !== other) {
            if (this.size != other.size) return false
            val areNotEqual = this.asSequence()
                .zip(other.asSequence())
                .map { (fromThis, fromOther) -> fromThis == fromOther }
                .contains(false)
            if (areNotEqual) return false
        }
        return true
    }


    private fun verifySeed() {

        if (listTemp.deepEqualTo(listverify)) {
            btnDone.alpha = 1F
            isCodeVerified = true
            textCodeVerify.text = getString(R.string.your_code_verified)
        } else {
        }

    }

    fun getPrivateKeyFromMnemonicCode(words: List<String?>?): String? {

        val path = "44H/714H/0H/0/0"

        val seed = MnemonicCode.toSeed(words, "")
        var key: DeterministicKey = HDKeyDerivation.createMasterPrivateKey(seed)
        val childNumbers = HDUtils.parsePath(path)
        for (cn in childNumbers) {
            key = HDKeyDerivation.deriveChildKey(key, cn)
        }
        return key.privateKeyAsHex
    }

    override fun onResume() {
        super.onResume()
        patternBack = 2
        (context as PatternActivity).consHeader.visibility = View.VISIBLE
    }

}
