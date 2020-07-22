package com.application.nodawallet.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.application.nodawallet.R
import com.application.nodawallet.activity.TransDetailsActivity
import com.application.nodawallet.model.*
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.CustomTextViewNormal
import com.application.nodawallet.utils.CustomTextViewSemiBold
import com.application.nodawallet.utils.UtilsDefault
import org.web3j.utils.Convert
import java.math.BigDecimal

class CurrencyinfoAdapter (val context: Context) : RecyclerView.Adapter<CurrencyinfoAdapter.ViewHolder>() {

    private var list = emptyList<TransactionHistory>()
    var toAddress = ""
    var coinsymbol = ""
    var cointype = ""
    var tokenDecimal = "18"
    var myadd = ""
    var balance = ""


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyinfoAdapter.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_currency_info, parent, false)
        return CurrencyinfoAdapter.ViewHolder(view)
    }


    override fun getItemCount(): Int {

        return list.size

    }

    override fun onBindViewHolder(holder: CurrencyinfoAdapter.ViewHolder, position: Int) {

        val model = list[position]

        if (coinsymbol == "BTC" || coinsymbol == "LTC"){
            holder.textHash.text = model.hash
            holder.textTransType.text = model.nonce
            val value =  model.value
            val number = BigDecimal(value!!.replace(",","."))
            holder.textValue.text =  number.stripTrailingZeros().toPlainString()+" "+coinsymbol
            Log.d("toadd",model.to)
            if (model.nonce == "Received"){
                holder.textValue.setTextColor(ContextCompat.getColor(context,R.color.stockgreen))
            }
            else {
                holder.textValue.setTextColor(ContextCompat.getColor(context,R.color.stockred))
            }
        }
        else if (coinsymbol == "BNB" || cointype =="BEP2"){
            holder.textHash.text = model.hash
            if (cointype == "BEP2"){
                var symbol = coinsymbol.split("-")
                val value =  model.value
                val number = BigDecimal(value!!.replace(",","."))
                holder.textValue.text =  number.stripTrailingZeros().toPlainString()+" "+symbol[0].toUpperCase()

            }
            else {
                val value =  model.value
                val number = BigDecimal(value!!.replace(",","."))
                holder.textValue.text =  number.stripTrailingZeros().toPlainString()+" "+coinsymbol

            }
            if (toAddress == model.to){
                Log.d("toadd",toAddress)
                holder.textTransType.text = "Received"
                holder.textValue.setTextColor(ContextCompat.getColor(context,R.color.stockgreen))
            }
            else {
                holder.textTransType.text = "Sent"

                holder.textValue.setTextColor(ContextCompat.getColor(context,R.color.stockred))
            }

            if (model.isError !=null){
                var data = ""

                if (model.input != "null"){
                    data = "("+model.input+")"
                }
                holder.textTransType.text = model.nonce+" "+data
                if (model.isError.contains("BUY")){
                    holder.textValue.setTextColor(ContextCompat.getColor(context,R.color.stockgreen))
                }
                else {
                    holder.textValue.setTextColor(ContextCompat.getColor(context,R.color.stockred))
                }
                if (model.value == null){
                    holder.textValue.text = ""
                }
            }

        }
        else{
            holder.textHash.text = model.hash
            if (model.tokenDecimal == "" || model.tokenDecimal == null){
                tokenDecimal ="18"
            }
            else {
                tokenDecimal = model.tokenDecimal
            }
            val numner = tokenDecimal
            val add = numner.toDouble()
            val add2= add.toInt()
            val nu = add2+1
            val formatted = String.format("%1$-" + nu + "s", "1").replace(' ', '0')

           val bal  = model.value!!.toDouble() / formatted.toDouble()
            balance = UtilsDefault.formatCryptoCurrency(bal.toString())
            val number = BigDecimal(balance.replace(",","."))

            holder.textValue.text =  number.stripTrailingZeros().toPlainString()+" "+coinsymbol
            // holder.textTransType.text = context.getString(R.string.withdraw)

            if (myadd == model.to){
                holder.textTransType.text = "Received"

                holder.textValue.setTextColor(ContextCompat.getColor(context,R.color.stockgreen))
            }
            else {
                holder.textTransType.text = "Sent"

                holder.textValue.setTextColor(ContextCompat.getColor(context,R.color.stockred))
            }


            if (model.isError =="1"){
               // holder.textTransType.setTextColor(ContextCompat.getColor(context,R.color.stockred))
                holder.textTransType.text = "Error"
                holder.textValue.setTextColor(ContextCompat.getColor(context,R.color.papertext))

            }
            else {
                if (coinsymbol == "ETH"){
                    if (model.input != "0x"){
                        holder.textTransType.text = "Smart Contract Call"
                        if (model.value.toDouble() == 0.0){
                            holder.textValue.text = "0.00 "+coinsymbol
                            holder.textValue.setTextColor(ContextCompat.getColor(context,R.color.papertext))
                        }
                    }
                }


            }
        }


        holder.llParent.setOnClickListener {


            val intent = Intent(context, TransDetailsActivity::class.java)
            intent.putExtra("from", model.from)
            intent.putExtra("to", model.to)
            intent.putExtra("gasprice", model.gasPrice)
            intent.putExtra("gasused", model.gasUsed)
            intent.putExtra("value", model.value)
            intent.putExtra("hash", model.hash)
            intent.putExtra("nonce", model.nonce)
            intent.putExtra("time", model.timeStamp)
            intent.putExtra("decimal", tokenDecimal)
            intent.putExtra("coinsymbol", coinsymbol)
            intent.putExtra("toAddress", toAddress)
            intent.putExtra("myadd", myadd)
            intent.putExtra("cointype", cointype)
            intent.putExtra("confirmations", model.confirmations)
            context.startActivity(intent)
        }

    }

    internal fun sethistory(words: ArrayList<TransactionHistory>,address:String,coinsymbol:String,cointype:String,myadd:String) {
        this.list = words
        this.toAddress = address
        this.coinsymbol = coinsymbol
        this.cointype = cointype
        this.myadd = myadd
        notifyDataSetChanged()
    }



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textHash = itemView.findViewById<CustomTextViewNormal>(R.id.textHash)
        val textTransType = itemView.findViewById<CustomTextViewNormal>(R.id.textTransType)
        val textValue = itemView.findViewById<CustomTextViewSemiBold>(R.id.textValue)
        val llParent = itemView.findViewById<LinearLayout>(R.id.llParent)


    }

}