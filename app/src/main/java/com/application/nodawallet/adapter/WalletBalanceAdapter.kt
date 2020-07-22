package com.application.nodawallet.adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Switch
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.application.nodawallet.R
import com.application.nodawallet.activity.MainActivity
import com.application.nodawallet.fragment.CurrencyDetailFragment
import com.application.nodawallet.model.AllWalletList
import com.application.nodawallet.model.CoinList
import com.application.nodawallet.model.MultiCoinList
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.CustomTextViewNormal
import com.application.nodawallet.utils.CustomTextViewSemiBold
import com.application.nodawallet.utils.UtilsDefault
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_token.*
import java.math.BigDecimal

class WalletBalanceAdapter (val context: Context) : RecyclerView.Adapter<WalletBalanceAdapter.ViewHolder>() {

    private var list = emptyList<CoinList>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletBalanceAdapter.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_wallet_balance, parent, false)
        return WalletBalanceAdapter.ViewHolder(view)
    }


    override fun getItemCount(): Int {

        return list.size

    }

    override fun onBindViewHolder(holder: WalletBalanceAdapter.ViewHolder, position: Int) {

        val model = list[position]
        holder.textCoinName.text = model.coinName

        if (model.enableStatus == 1){
            holder.viewToken.visibility = View.VISIBLE
            holder.consCoin.visibility = View.VISIBLE
        }
        else {
            holder.viewToken.visibility = View.GONE
            holder.consCoin.visibility = View.GONE
        }

        try {
            if (model.tokenImage.isNotEmpty()){
                Picasso.with(context).load(model.tokenImage).into(holder.imgCoin)

            }
        }
        catch (e:Exception){
            holder.imgCoin.setImageResource(R.drawable.erc_token)

            e.printStackTrace()
        }


        try {
            if (model.marketPrice !="" && model.marketPrice !="null"){
                val currency = UtilsDefault.getSharedPreferenceString(Constants.CURRENCY)
                val symbol =  currency?.split("-")!![1]
                if (symbol == "BTC"){
                    holder.textCoinValue.text = UtilsDefault.formatCryptoCurrency(model.marketPrice)+" "+symbol
                }
                else {
                    holder.textCoinValue.text = UtilsDefault.formatMarket(model.marketPrice)+" "+symbol
                }

                if (model.marketPercentage == "null"){

                }
                else {
                    holder.textMarketPercentage.text = UtilsDefault.formatDecimal(model.marketPercentage)+" %"

                }

                val market = model.marketPercentage.toDouble()
                if (market >= 0){
                    holder.textMarketPercentage.setTextColor(ContextCompat.getColor(context,R.color.stockgreen))
                }
                else {
                    holder.textMarketPercentage.setTextColor(ContextCompat.getColor(context,R.color.stockred))

                }
            }
        }
        catch (e:Exception){
            e.printStackTrace()
        }

        try {
           /* val numner = model.decimal
            val add = numner.toDouble()
            val add2= add.toInt()
            val nu = add2+1
            val formatted = String.format("%1$-" + nu + "s", "1").replace(' ', '0')

            val bal  = model.balance.toDouble() / formatted.toDouble()
            holder.textBal.text = UtilsDefault.formatCryptoCurrency(bal.toString())+" "+model.coinSymbol*/

            if (model.coinType == "BEP2"){
                val number = BigDecimal(model.balance.replace(",","."))
                holder.textBal.text =  number.stripTrailingZeros().toPlainString()+" "+model.coinName.toUpperCase()

            }
            else {
                val value = UtilsDefault.formatCryptoCurrency(model.balance)
                val number = BigDecimal(value.replace(",","."))
                holder.textBal.text =  number.stripTrailingZeros().toPlainString()+" "+model.coinSymbol
                if (value.toDouble() == 0.0){
                    holder.textBal.text = "0.00 "+model.coinSymbol
                }

            }
            val currency = UtilsDefault.getSharedPreferenceString(Constants.CURRENCY)
            val symbol =  currency?.split("-")!![1]
            val eqvalue = (model.balance.toDouble()*model.marketPrice.toDouble()).toString()
            if (symbol == "BTC"){
                holder.textEqvValue.text = UtilsDefault.formatCryptoCurrency(eqvalue)+" "+symbol
            }
            else {
                holder.textEqvValue.text = UtilsDefault.formatDecimal(eqvalue)+" "+symbol
            }


        }
        catch (e:Exception){
            e.printStackTrace()
        }

        holder.consCoin.setOnClickListener {

            val bundle = Bundle()
            val fragment = CurrencyDetailFragment()
            bundle.putString("coinName",model.coinName)
            bundle.putString("marketPrice",model.marketPrice)
            bundle.putString("marketPercentage",model.marketPercentage)
            bundle.putString("address",model.publicAddress)
            fragment.arguments = bundle
            (context as MainActivity).push(fragment)
        }

    }

    internal fun setCoins(words: List<CoinList>) {
        this.list = words
        notifyDataSetChanged()
    }



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imgCoin =  itemView.findViewById<ImageView>(R.id.imgCoin)
        var textBal =  itemView.findViewById<CustomTextViewNormal>(R.id.textBal)
        var textEqvValue =  itemView.findViewById<CustomTextViewNormal>(R.id.textEqvValue)
        var consCoin =  itemView.findViewById<RelativeLayout>(R.id.consCoin)
        var textCoinName =  itemView.findViewById<CustomTextViewNormal>(R.id.textCoinName)
        var textCoinValue =  itemView.findViewById<CustomTextViewNormal>(R.id.textCoinValue)
        var textMarketPercentage =  itemView.findViewById<CustomTextViewNormal>(R.id.textMarketPercentage)
        var llToken =  itemView.findViewById<LinearLayout>(R.id.llToken)
        var viewToken =  itemView.findViewById<View>(R.id.viewToken)


    }

}