package com.application.nodawallet.activity

import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.application.nodawallet.R
import com.application.nodawallet.application.NodaApplication
import com.application.nodawallet.model.*
import com.application.nodawallet.retrofit.NodaWalletAPI
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.UtilsDefault
import com.application.nodawallet.viewmodel.MultiCoinViewModel
import com.application.nodawallet.viewmodel.MultiWalletViewModel
import com.binance.dex.api.client.BinanceDexEnvironment
import com.binance.dex.api.client.Wallet
import kotlinx.android.synthetic.main.activity_multiwalletimport.*
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
import javax.inject.Inject

class MultiWalletImportActivity : BaseActivity() {

    private lateinit var viewmodel: MultiWalletViewModel
    private lateinit var viewCoinmodel: MultiCoinViewModel
    var size = 0
    var isAddClicked = false
    val coinlist = ArrayList<CoinList>()
    var notifyList = ArrayList<CurrencyNofify>()

    @Inject
    lateinit var nodaWalletAPI: NodaWalletAPI

    var balanceResponse: BalanceResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiwalletimport)

        NodaApplication.instance.mComponent.inject(this)



        viewmodel = ViewModelProvider(this).get(MultiWalletViewModel::class.java)
        viewCoinmodel = ViewModelProvider(this).get(MultiCoinViewModel::class.java)
        viewmodel.getAllData.observe(this,
            Observer<List<MultiWalletList>> { t ->
                size = t.size
                try {
                    if (isAddClicked) {
                        isAddClicked = false
                        insertCoin(t[size.minus(1)].id)
                    }
                } catch (e: ArrayIndexOutOfBoundsException) {
                    if (isAddClicked) {
                        isAddClicked = false
                        insertCoin(t[size].id)
                    }
                    e.printStackTrace()
                }

            })

        val clipBoardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        textPaste.setOnClickListener {
            val text = clipBoardManager.primaryClip?.getItemAt(0)?.text.toString()
            edtPhrase.setText(text)
        }

        btnImport.setOnClickListener {

            val phrase = edtPhrase.text.toString().trim()

            if (phrase == "") {
                Toast.makeText(this, getString(R.string.empty_phrase), Toast.LENGTH_SHORT).show()
            } else if (!MnemonicUtils.validateMnemonic(phrase)) {
                Toast.makeText(this, getString(R.string.invalid_phrase), Toast.LENGTH_SHORT).show()
            } else {
                val wallet = MultiWalletList(
                    getString(R.string.multi_coin_wallet) + size.plus(1),
                    R.drawable.ic_round_wallet,
                    0,
                    phrase,
                    "multi"
                )
                viewmodel.insert(wallet)
                UtilsDefault.updateSharedPreferenceInt(Constants.CODE_VERIFY, 1)
                UtilsDefault.updateSharedPreferenceString(
                    Constants.WALLETNAME,
                    getString(R.string.multi_coin_wallet) + size.plus(1)
                )

                isAddClicked = true

            }

        }

        imgBackMulti.setOnClickListener {
            onBackPressed()

        }
    }


    private fun insertCoin(id: Int?) {
        coinlist.clear()
        AddETH(id)
    }

    private fun AddBTC(id: Int?) {

        val params = MainNetParams.get()
        val time = System.currentTimeMillis() / 1000

        val mnemonic = edtPhrase.text.toString().trim()
        val seede = DeterministicSeed(mnemonic, null, "", time)

        val chain = DeterministicKeyChain.builder().seed(seede).build()
        val keypath = HDUtils.parsePath("M/44H/0H/0H/0/0") //mainnet
        val key = chain.getKeyByPath(keypath, true)
        val privateKey = key.getPrivateKeyEncoded(params)
        val publicKey = key.publicKeyAsHex
        val address = LegacyAddress.fromKey(params, key).toBase58()



        coinlist.add(
            CoinList(
                "Bitcoin",
                R.drawable.ic_btc,
                 "https://ww.btc.com/png",
                "BTC",
                mnemonic,
                "Coin",
                address,
                privateKey.toString(),
                publicKey.toString(),
                1,
                "0.000",
                "0.00",
                "0.00",
                "8",
                "bitcoin",
                0.0,
                0.0,
                0.0
            )
        )

        notifyList.add(CurrencyNofify(address, "BTC"))
        AddLTC(id)

    }

    private fun AddLTC(id: Int?) {


        val params = LitecoinMainNetParams.get()
        val time = System.currentTimeMillis() / 1000

        val mnemonic = edtPhrase.text.toString().trim()
        val seede = DeterministicSeed(mnemonic, null, "", time)

        val chain = DeterministicKeyChain.builder().seed(seede).build()
        val keypath = HDUtils.parsePath("M/44H/2H/0H/0/0") //mainnet
        val key = chain.getKeyByPath(keypath, true)
        val privateKey = key.getPrivateKeyEncoded(params)
        val publicKey = key.publicKeyAsHex
        val address = LegacyAddress.fromKey(params, key).toBase58()



        coinlist.add(CoinList(
                "Litecoin",
                R.drawable.ic_ltc,
                 "https://ww.ltc.com/png",
                "LTC",
                mnemonic,
                "Coin",
                address,
                privateKey.toString(),
                publicKey.toString(),
                1,
                "0.000",
                "0.00",
                "0.00",
                "8",
                "litecoin",
                0.0,
                0.0,
                0.0
            )
        )

        notifyList.add(CurrencyNofify(address, "LTC"))
        AddBNB(id)


    }

    private fun AddBNB(id: Int?) {
        val mnemonic = edtPhrase.text.toString().trim()
        val lstValues: List<String> = mnemonic.split(" ").map { it -> it.trim() }
        val privatekey = getPrivateKeyFromMnemonicCode(lstValues)
        val wallet = Wallet(privatekey, BinanceDexEnvironment.PROD)

        coinlist.add(CoinList(
                "Binance",
                R.drawable.binance,
                "https://ww.bnb.com/png",
                "BNB",
                mnemonic,
                "Coin",
                wallet.address,
                privatekey.toString(),
                Numeric.toHexStringNoPrefix(wallet.pubKeyForSign),
                1,
                "0.000",
                "0.00",
                "0.00",
                "8",
                "binancecoin",
                0.0,
                0.0,
                0.0
            )
        )

        val updatelist: List<CoinList> = ArrayList<CoinList>(coinlist)
        val listcoin = MultiCoinList(id!!, updatelist)
        viewCoinmodel.insert(listcoin)

        notifyList.add(CurrencyNofify(wallet.address, "BNB"))

        UtilsDefault.updateSharedPreferenceInt(Constants.COINID, id)

        if (UtilsDefault.getSharedPreferenceString(Constants.CURRENCY) == null) {
            UtilsDefault.updateSharedPreferenceString(Constants.CURRENCY, "USD-$")

        }

        if (UtilsDefault.getSharedPreferenceString(Constants.PUSHNOTIFICATION) == null) {
            UtilsDefault.updateSharedPreferenceString(Constants.PUSHNOTIFICATION, "enable")
        }

        hideProgress()
        val intent = Intent(this@MultiWalletImportActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()


    }

    private fun AddETH(id: Int?) {

        showProgress()
        val mnemonic = edtPhrase.text.toString().trim()
        val derivationPath = intArrayOf(
            44 or Bip32ECKeyPair.HARDENED_BIT,
            60 or Bip32ECKeyPair.HARDENED_BIT,
            0 or Bip32ECKeyPair.HARDENED_BIT,
            0,
            0
        )
        val masterkeypair = Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(mnemonic, ""))
        val derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterkeypair, derivationPath)

        val credentials = Credentials.create(derivedKeyPair)


        coinlist.add(
            CoinList(
                "Ethereum",
                R.drawable.ic_eth,
                 "https://ww.eth.com/png",
                "ETH",
                mnemonic,
                "Coin",
                credentials.address,
                Numeric.toHexStringNoPrefix(credentials.ecKeyPair.privateKey),
                credentials.ecKeyPair.publicKey.toString(),
                1,
                "0.000",
                "0.00",
                "0.00",
                "18",
                "ethereum",
                0.0,
                0.0,
                0.0
            )
        )

        notifyList.clear()
        notifyList.add(CurrencyNofify(credentials?.address, "ETH"))
        AddBTC(id)


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


    override fun onBackPressed() {
        super.onBackPressed()
    }
}
