package com.application.nodawallet.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.application.nodawallet.R
import com.application.nodawallet.application.NodaApplication
import com.application.nodawallet.model.HashResponse
import com.application.nodawallet.retrofit.NodaWalletAPI
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.UtilsDefault
import com.google.firebase.crashlytics.core.CrashlyticsCore
import kotlinx.android.synthetic.main.activity_confirm_deposit.*
import kotlinx.android.synthetic.main.activity_trans_details.*
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Convert
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

class   TransDetailsActivity : BaseActivity() {

    var from = ""
    var to = ""
    var hash = ""
    var value = ""
    var gas = ""
    var nonce = ""
    var time = ""
    var content = ""
    var decimal = ""
    var coinsymbol = ""
    var transType = ""
    var baseurl = ""
    var gasused = ""
    var toAddress = ""
    var myadd = ""
    var confirmations = ""
    var cointype = ""
    var hashResponse:HashResponse? = null

    @Inject
    lateinit var nodaWalletAPI: NodaWalletAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trans_details)

        NodaApplication.instance.mComponent.inject(this)


        val intent = intent
        if (intent !=null){
            from = intent.getStringExtra("from") ?: ""
            to = intent.getStringExtra("to") ?: ""
            hash = intent.getStringExtra("hash") ?: ""
            value = intent.getStringExtra("value") ?: ""
            gas = intent.getStringExtra("gasprice") ?: ""
            time = intent.getStringExtra("time") ?: ""
            nonce = intent.getStringExtra("nonce") ?: ""
            decimal = intent.getStringExtra("decimal") ?: ""
            coinsymbol = intent.getStringExtra("coinsymbol") ?: ""
            toAddress = intent.getStringExtra("toAddress") ?: ""
            gasused = intent.getStringExtra("gasused") ?: ""
            myadd = intent.getStringExtra("myadd") ?: ""
            cointype = intent.getStringExtra("cointype") ?: ""
            confirmations = intent.getStringExtra("confirmations") ?: ""
        }

        if (coinsymbol == "BTC"){
            baseurl = Constants.BTC_TX
            getHashDetails(coinsymbol,hash)

        }
        else if (coinsymbol == "LTC"){
            baseurl = Constants.LTC_TX
            getHashDetails(coinsymbol,hash)
        }
        else if (coinsymbol == "BNB"|| cointype == "BEP2"){
            baseurl = Constants.BNB_TX
            getBnbHash()
        }
        else {
            baseurl = Constants.ETHERSCAN_TX
            getEthHash(gas,gasused)
        }


        textMore.setOnClickListener {
            val uri: Uri = Uri.parse(baseurl+hash)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        imgBackDetails.setOnClickListener {
            finish()
        }

     //   textTransTo.text = to
        /*val content = SpannableString(hash)
        content.setSpan(UnderlineSpan(),0,content.length,0)*/

        /*val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val cal = Calendar.getInstance()
        cal.timeInMillis = time.toLong()
        val date = sdf.format(cal.time)
        textTransTime.text = time*/


    }

    private fun getBnbHash() {
        textConf.visibility = View.INVISIBLE
        textTime.text = time.replace("T", " ")
        textValue.text = value+" "+coinsymbol
        textNetworkfee.text = gas

        if (toAddress == to){
            textRecep.text = getString(R.string.sender)
            textRecipient.text = from
        }
        else {
            textRecep.text = getString(R.string.recipient)
            textRecipient.text = to
        }

        if (value == ""){
            textValue.visibility = View.INVISIBLE
        }
        if (gas == ""){
            textNetworkfee.text = "0.00"
        }
        if (to == ""){
            textRecipient.text = from
        }

    }

    private fun getEthHash(gas: String, gasused: String) {
        textTime.text = epochTime(time)
        textConfirmations.text = confirmations
        val numner = decimal
        val add = numner.toDouble()
        val add2= add.toInt()
        val nu = add2+1
        val formattedamount = String.format("%1$-" + nu + "s", "1").replace(' ', '0')

        val balance  = value.toDouble() / formattedamount.toDouble()
        textValue.text = UtilsDefault.formatCryptoCurrency(balance.toString().replace(",","."))+" "+coinsymbol
        val fees =  gas.toDouble()*gasused.toDouble()
        val fee = fees/1000000000000000000
        textNetworkfee.text = UtilsDefault.formatCryptoCurrency(fee.toString())

        if (myadd != to){
            textRecep.text = getString(R.string.recipient)
            textRecipient.text = to

        }
        else {
            textRecep.text = getString(R.string.sender)
            textRecipient.text = from

        }


    }

    private fun getHashDetails(coinsymbol: String,hash:String) {


        val url = "https://sochain.com/api/v2/get_tx/$coinsymbol/$hash"

        nodaWalletAPI.gethash(url).enqueue(object : Callback<HashResponse>{
            override fun onFailure(call: Call<HashResponse>, t: Throwable) {

            }

            override fun onResponse(call: Call<HashResponse>, response: Response<HashResponse>) {

                hashResponse = response.body()

                if (hashResponse!=null && hashResponse?.status == "success"){

                    if (hashResponse?.data !=null){

                        textTime.text = epochTime(hashResponse?.data!!.time.toString())
                        textConfirmations.text = hashResponse?.data!!.confirmations.toString()
                        var outputs = 0.0
                        var inputs = 0.0

                        for (i in 0..hashResponse?.data!!.outputs!!.size.minus(1)){

                            outputs+= hashResponse?.data!!.outputs!![i].value!!.toDouble()
                        }

                        for (i in 0..hashResponse?.data!!.inputs!!.size.minus(1)){

                            inputs+= hashResponse?.data!!.inputs!![i].value!!.toDouble()
                        }

                        if (nonce == "Received"){
                            textRecep.text = getString(R.string.sender)
                            val fee = inputs - outputs
                            textRecipient.text = hashResponse?.data!!.inputs!![0].address
                            textNetworkfee?.text = ""+UtilsDefault.formatCryptoCurrency(fee.toString())
                            textValue?.text = ""+value+" "+coinsymbol

                        }
                        else {
                            textRecep.text = getString(R.string.recipient)
                            textRecipient.text = hashResponse?.data!!.outputs!![0].address
                            val fee = inputs - outputs
                            textNetworkfee?.text = ""+UtilsDefault.formatCryptoCurrency(fee.toString())
                            textValue?.text = ""+value+" "+coinsymbol

                        }

                    }
                }


            }

        })

    }

    fun epochTime(time:String) : String{
        val epoch = time.toLong()
        val date = Date(epoch*1000L)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        format.timeZone = TimeZone.getTimeZone("Etc/UTC")
        val formatted = format.format(date)
        Log.d("conv",formatted)
        return formatted
    }
}
