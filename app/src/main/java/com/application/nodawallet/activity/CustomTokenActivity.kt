package com.application.nodawallet.activity

import android.Manifest
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.application.nodawallet.R
import com.application.nodawallet.application.NodaApplication
import com.application.nodawallet.model.*
import com.application.nodawallet.retrofit.NodaWalletAPI
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.PermissionUtils
import com.application.nodawallet.utils.UtilsDefault
import com.application.nodawallet.viewmodel.MultiCoinViewModel
import kotlinx.android.synthetic.main.activity_custom_token.*
import org.web3j.crypto.Bip32ECKeyPair
import org.web3j.crypto.Credentials
import org.web3j.crypto.MnemonicUtils
import org.web3j.crypto.WalletUtils
import org.web3j.utils.Numeric
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class CustomTokenActivity : BaseActivity() {

    private lateinit var viewCoinmodel: MultiCoinViewModel
    val multicoinlist = ArrayList<CoinList>()
    var walletId = 0

    @Inject
    lateinit var nodaWalletAPI: NodaWalletAPI

    var contractVerify: ContractVerify? = null
    var PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_token)

        NodaApplication.instance.mComponent.inject(this)


        val clipBoardManager = getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager

        textTokenPaste.setOnClickListener {
            val text = clipBoardManager.primaryClip?.getItemAt(0)?.text.toString()
            edtAddress.setText(text)
        }

        mScanBtn.setOnClickListener {
            PermissionUtils().permissionList(
                PERMISSIONS,
                this,
                object : PermissionUtils.PermissionCheck {
                    override fun onSuccess(result: String?) {
                        startActivityForResult(
                            Intent(
                                this@CustomTokenActivity,
                                ScanActivity::class.java
                            ), 2
                        )

                    }

                })


        }

        viewCoinmodel = ViewModelProvider(this).get(MultiCoinViewModel::class.java)
        try {
            viewCoinmodel.getDataById.observe(this,
                Observer<List<MultiCoinList>> { coinlist ->

                    try {

                        if (coinlist.isNotEmpty() && coinlist[0].list.isNotEmpty()) {

                            multicoinlist.clear()
                            multicoinlist.addAll(coinlist[0].list)
                            walletId = coinlist[0].id
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                })

        } catch (e: Exception) {
            e.printStackTrace()
        }

        edtAddress.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val value = s.toString()
                if (value != "") {
                    if (WalletUtils.isValidAddress(value)) {
                        getTokenInfo(value)
                    }
                }
            }

        })

        btnDone.setOnClickListener {
            val address = edtAddress.text.toString().trim()
            val name = edtName.text.toString().trim()
            val symbol = edtSymbol.text.toString().trim()
            val decimal = edtDecimal.text.toString().trim()

            if (address == "") {
                edtAddress.error = getString(R.string.address_empty)
            } else if (!WalletUtils.isValidAddress(address)) {
                Toast.makeText(
                    this@CustomTokenActivity,
                    getString(R.string.invalid_contract),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (name == "" || name.length < 3) {
                edtName.error = getString(R.string.name_above)
            } else if (symbol == "") {
                edtSymbol.error = getString(R.string.invalid_symbol)
            } else if (decimal.toDouble() <= 0) {
                edtDecimal.error = getString(R.string.invalid_decimal)
            } else {
                if (UtilsDefault.isOnline()) {
                    getTokenMarket(address, name, symbol, decimal, "ETH")

                }
            }

        }

        LinearLayout1.setOnClickListener {
            startActivity(Intent(this, NetworktokenActivity::class.java))
        }

        imgBackCustomToken.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getTokenMarket(
        address: String,
        name: String,
        symbol: String,
        decimal: String,
        network: String
    ) {
        showProgress()
        val url = "https://api.coingecko.com/api/v3/coins/list"
        nodaWalletAPI.coinlist(url).enqueue(object : Callback<List<CoinListResponse>> {
            override fun onFailure(call: Call<List<CoinListResponse>>, t: Throwable) {
                hideProgress()
                addToken(address, name, symbol, decimal, network, "0.00", "0.00", "")

            }

            override fun onResponse(
                call: Call<List<CoinListResponse>>,
                response: Response<List<CoinListResponse>>
            ) {
                val coinlist = response.body()
                var coinId = ""
                if (coinlist!!.isNotEmpty()) {
                    for (i in 0..coinlist.size.minus(1)) {

                        if (coinlist[i].symbol == symbol.toLowerCase()) {

                            coinId = coinlist[i].id
                            break

                        }
                    }
                    getMarketPrice(network, name, decimal, address, coinId)

                } else {
                    addToken(address, name, symbol, decimal, network, "0.00", "0.00", "")

                }

            }

        })
    }

    private fun getMarketPrice(
        network: String,
        name: String,
        decimal: String,
        address: String,
        id: String
    ) {
        if (id != "") {
            val currency = UtilsDefault.getSharedPreferenceString(Constants.CURRENCY)
            val symbol = currency?.split("-")!![0]
            val url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=$symbol&ids=$id&order=market_cap_desc"
            var marketprice = ""
            var marketpercent = ""
            var image = ""
            var networkId = ""

            nodaWalletAPI.getmarketPrice(url).enqueue(object : Callback<List<MarketResponse>> {
                override fun onFailure(call: Call<List<MarketResponse>>, t: Throwable) {
                    hideProgress()
                    addToken(address, name, symbol, decimal, network, "0.00", "0.00", "")

                }

                override fun onResponse(
                    call: Call<List<MarketResponse>>,
                    response: Response<List<MarketResponse>>
                ) {
                    hideProgress()
                    val marketResponse = response.body()
                    if (marketResponse!!.isNotEmpty()) {
                        marketprice = marketResponse[0].current_price.toString()
                        marketpercent = marketResponse[0].price_change_percentage_24h.toString()
                        image = marketResponse[0].image.toString()
                        networkId = marketResponse[0].id.toString()
                        addToken(
                            address,
                            name,
                            marketResponse[0].symbol!!.toUpperCase(),
                            decimal,
                            networkId,
                            marketprice,
                            marketpercent,
                            image
                        )
                    } else {
                        addToken(address, name, symbol, decimal, network, "0.00", "0.00", "")

                    }
                }

            })
        } else {
            val symbol = edtSymbol.text.toString().trim()
            addToken(address, name, symbol, decimal, network, "0.00", "0.00", "")

        }


    }

    private fun getTokenInfo(address: String) {

        val url = "${Constants.API_ETHERSCAN}?module=account&action=tokentx&contractaddress=$address&page=1&offset=2&apikey=${Constants.APIKEY}"

        nodaWalletAPI.tokeninfo(url).enqueue(object : Callback<TokenInformation> {
            override fun onFailure(call: Call<TokenInformation>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<TokenInformation>,
                response: Response<TokenInformation>
            ) {
                val tokeresponse = response.body()
                if (tokeresponse?.status == "1") {
                    val tokenlist = tokeresponse.result
                    if (tokenlist!!.isNotEmpty()) {
                        edtName.setText(tokenlist[0].tokenName)
                        edtSymbol.setText(tokenlist[0].tokenSymbol)
                        edtDecimal.setText(tokenlist[0].tokenDecimal)
                    }

                }

            }

        })


    }

    private fun tokenVerify(
        address: String,
        name: String,
        symbol: String,
        decimal: String,
        network: String
    ) {

        val url =
            "${Constants.API_ETHERSCAN}?module=contract&action=getabi&address=$address&apikey=${Constants.APIKEY}"

        nodaWalletAPI.contractverify(url).enqueue(object : Callback<ContractVerify> {
            override fun onFailure(call: Call<ContractVerify>, t: Throwable) {
                Toast.makeText(
                    this@CustomTokenActivity,
                    getString(R.string.server_error),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(
                call: Call<ContractVerify>,
                response: Response<ContractVerify>
            ) {

                contractVerify = response.body()

                if (contractVerify?.status == "1") {
                    //addToken(address,name,symbol,decimal,network)
                } else {
                    Toast.makeText(
                        this@CustomTokenActivity,
                        getString(R.string.invalid_contract),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            val value = data.getStringExtra("barcode")
            edtAddress.post {
                var s = value
                val ss = s.replace("ethereum:", "")
                val s1 = ss.replace("bitcoin:", "")
                Log.e("TAG", "string1====" + s1)

                val l = s1.length
                if (s1.indexOf("?") != -1) {
                    val s2 = s1.indexOf("?")
                    Log.e("TAG", "removewamout====" + s2)
                    s = s1.removeRange(s2, l)
                    Log.e("TAG", "final====" + s)

                } else {
                    s = s1
                }
                edtAddress.setText(s)

            }
        }

    }

    private fun addToken(
        address: String,
        name: String,
        symbol: String,
        decimal: String,
        network: String, marketPrice: String, marketPercent: String, image: String
    ) {

        val colist = ArrayList<CoinList>()
        var phrase = ""

        colist.clear()
        for (i in 0..multicoinlist.size.minus(1)) {
            colist.add(
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

            phrase = multicoinlist[0].phrase
        }
        val mnemonic = phrase
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


        colist.add(
            CoinList(
                name,
                R.drawable.erc_token,
                image,
                symbol,
                mnemonic,
                "token",
                address,
                Numeric.toHexStringNoPrefix(credentials.ecKeyPair.privateKey),
                credentials.ecKeyPair.publicKey.toString(),
                1,
                "0.0",
                marketPrice,
                marketPercent,
                decimal,
                network,
                0.0,
                0.0,
                0.0
            )
        )

        viewCoinmodel.update(colist, walletId)
        finish()

    }

}
