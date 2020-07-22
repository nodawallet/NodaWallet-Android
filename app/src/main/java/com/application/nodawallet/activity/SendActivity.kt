package com.application.nodawallet.activity

import android.Manifest
import android.content.ClipboardManager
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.application.nodawallet.R
import com.application.nodawallet.application.NodaApplication
import com.application.nodawallet.model.GetCurrencyList
import com.application.nodawallet.model.LitecoinMainNetParams
import com.application.nodawallet.retrofit.NodaWalletAPI
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.PermissionUtils
import com.application.nodawallet.utils.UtilsDefault
import com.google.android.gms.vision.barcode.Barcode
import kotlinx.android.synthetic.main.activity_confirm_deposit.*
import kotlinx.android.synthetic.main.activity_send.*
import kotlinx.android.synthetic.main.activity_send.imgBackSend
import kotlinx.android.synthetic.main.activity_send.textMax
import kotlinx.android.synthetic.main.fragment_exchange.*
import kotlinx.android.synthetic.main.fragment_single_coin_address.*
import org.bitcoinj.core.Address
import org.bitcoinj.core.AddressFormatException
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.TestNet3Params
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.crypto.*
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.http.HttpService
import org.web3j.tx.ChainId
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.StaticGasProvider
import org.web3j.tx.response.NoOpProcessor
import org.web3j.utils.Convert
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.text.DateFormatSymbols
import java.util.*
import javax.inject.Inject

class SendActivity : BaseActivity() {

    var key = ""
    var receiveAddress = ""
    var depoAmount = ""
    var balanceAmount = ""
    var cointype = ""
    var decimal = ""
    var coinsymbol = ""
    var pubaddress = ""
    var marketprice = ""
    var min = "0.0001"
    var max = "1000"
    var mybal =""

    var PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @Inject
    lateinit var nodaWalletAPI: NodaWalletAPI



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)

        NodaApplication.instance.mComponent.inject(this)


        val intent = intent
        if (intent != null) {
            key = intent.getStringExtra("key") ?: ""
            balanceAmount = intent.getStringExtra("balance") ?: ""
            cointype = intent.getStringExtra("cointype") ?: ""
            decimal = intent.getStringExtra("decimal") ?: ""
            coinsymbol = intent.getStringExtra("coinsymbol") ?: ""
            pubaddress = intent.getStringExtra("address") ?: ""
            marketprice = intent.getStringExtra("marketprice") ?: ""
        }


        textMax.setOnClickListener {
            edtAmount.setText(UtilsDefault.formatCryptoCurrency(balanceAmount))
        }
        edtAmount.hint = getString(R.string.amount_in)+coinsymbol

        val currency = UtilsDefault.getSharedPreferenceString(Constants.CURRENCY)
        val symbol =  currency?.split("-")!![1]

        edtAmount.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString().replace(",",".").trim()
                if (text!=""){
                    textMarketAmount?.visibility = View.VISIBLE
                    val amount = text.toDouble()
                    val market = marketprice.toDouble()
                    val final = amount*market
                    textMarketAmount?.text = "~"+symbol+UtilsDefault.formatDecimal(final.toString())
                }
                else {
                    textMarketAmount?.visibility = View.INVISIBLE

                }
            }

        })

        mScanBtn.setOnClickListener {

            PermissionUtils().permissionList(PERMISSIONS,this,object : PermissionUtils.PermissionCheck{
                override fun onSuccess(result: String?) {
                    startActivityForResult(Intent(this@SendActivity, ScanActivity::class.java),2)
                }

            })

        }

        val clipBoardManager = getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
        /* clipBoardManager.addPrimaryClipChangedListener {
             val copiedString = clipBoardManager.primaryClip?.getItemAt(0)?.text?.toString()
             edtAddress.setText(copiedString)
         }*/

        textPasteAddress.setOnClickListener {
            val text = clipBoardManager.primaryClip?.getItemAt(0)?.text.toString()
            edtReceiveAddress.setText(text)
        }


        btnSend.setOnClickListener {

             receiveAddress = edtReceiveAddress.text.toString().trim()
             depoAmount = edtAmount.text.toString().replace(",",".").trim()

            if (pubaddress == receiveAddress){
                Toast.makeText(this,getString(R.string.invalid_address),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (depoAmount == ""){
                return@setOnClickListener
            }
            val balance = balanceAmount.toDouble()


            if(balance == 0.0 || balance < depoAmount.toDouble()){
                Toast.makeText(this,getString(R.string.insufficient_bal),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            if (coinsymbol == "BTC" || coinsymbol == "LTC"){
                if (!isValidAddress(receiveAddress,coinsymbol)){
                    Toast.makeText(this,getString(R.string.invalid_address),Toast.LENGTH_SHORT).show()
                    return@setOnClickListener

                }
            }
            else if (coinsymbol == "BNB"|| cointype == "BEP2"){
                if (!isValidBinance(receiveAddress)){
                    Toast.makeText(this,getString(R.string.invalid_address),Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }
            else {
                if (!WalletUtils.isValidAddress(receiveAddress)){
                    Toast.makeText(this,getString(R.string.invalid_address),Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            if (depoAmount == ""){
                Toast.makeText(this,getString(R.string.enter_amount),Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            val dd = depoAmount.replace(",",".").trim()
            val depo = dd.toDouble()


            if (depo <= 0){
                Toast.makeText(this,getString(R.string.enter_amount_greater0),Toast.LENGTH_SHORT).show()
            }
            else if (depo < min.toDouble() || depo > max.toDouble()){
                Toast.makeText(this,getString(R.string.amount_should_minimum)+min +getString(R.string.and_maximum)+max,Toast.LENGTH_SHORT).show()
            }
            else {
                val intent = Intent(this,ConfirmDepositActivity::class.java)
                intent.putExtra("key",key)
                intent.putExtra("receiveAddress",receiveAddress)
                intent.putExtra("depoAmount",depoAmount)
                intent.putExtra("balance",balanceAmount)
                intent.putExtra("cointype",cointype)
                intent.putExtra("address",pubaddress)
                intent.putExtra("decimal",decimal)
                intent.putExtra("coinsymbol",coinsymbol)
                intent.putExtra("marketprice",marketprice)
                startActivity(intent)

                edtReceiveAddress.setText("")
                edtAmount.setText("")
            }


        }


        imgBackSend.setOnClickListener {
            onBackPressed()
        }


    }

    fun isValidBinance(address:String):Boolean{
        val address = com.paytomat.binance.Address.isValid(address)
        return address
    }


    fun isValidAddress(addres:String,symbol:String) :Boolean {
        return try {
            if (symbol == "BTC"){
              val  params = MainNetParams.get()
                Address.fromString(params, addres)
            }
            else {
                val  params = LitecoinMainNetParams.get()
                Address.fromString(params, addres)
            }
            true
        } catch (e:AddressFormatException){
            e.printStackTrace()
            false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            val value = data.getStringExtra("barcode")
            edtReceiveAddress.post {
                var s = value
                var ss = s.replace("ethereum:", "")
                var s1 = ss.replace("bitcoin:", "")
                Log.e("TAG", "string1====" + s1)

                var l = s1.length
                if (s1.indexOf("?") != -1) {
                    var s2 = s1.indexOf("?")
                    Log.e("TAG", "removewamout====" + s2)
                    s = s1.removeRange(s2, l)
                    Log.e("TAG", "final====" + s)

                } else {
                    s = s1
                }
                edtReceiveAddress.setText(s)

            }

        }

    }


    override fun onBackPressed() {
        super.onBackPressed()
    }
}
