package com.application.nodawallet.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.application.nodawallet.R
import com.application.nodawallet.model.CoinList
import com.application.nodawallet.utils.CustomTextViewNormal
import com.application.nodawallet.utils.CustomTextViewSemiBold
import com.application.nodawallet.utils.UtilsDefault
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_exchange.*
import kotlinx.android.synthetic.main.item_exchange_coin.view.*

class
ExchangeCoinAdapter(var context: Context) : RecyclerView.Adapter<ExchangeCoinAdapter.viewHolder>() {

    private var walletList = emptyList<CoinList>()

    var onItemClick: ((pos: Int, view: View) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exchange_coin, parent, false)

        return viewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return walletList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        val model:CoinList = walletList[position]

        holder.textCoinName.text = model.coinSymbol

        /*val numner = model.decimal
        val add = numner.toDouble()
        val add2= add.toInt()
        val nu = add2+1
        val formatted = String.format("%1$-" + nu + "s", "1").replace(' ', '0');
        Log.d("lead",formatted)
        val bal  = model.balance.toDouble() / formatted.toDouble()*/

        holder.textBal?.text = UtilsDefault.formatCryptoCurrency(model.balance.toString())+" "+model.coinSymbol

        try {
            Picasso.with(context).load(model.tokenImage).into(holder.imgCoin)
        }
        catch (e:Exception){
            e.printStackTrace()
        }

        holder.consParent.setOnClickListener {
            onItemClick?.invoke(position,it)
        }


    }

    internal fun setCoins(words: List<CoinList>) {
        this.walletList = words
        notifyDataSetChanged()
    }


    inner class viewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var textCoinName = view.findViewById<CustomTextViewSemiBold>(R.id.textCoinName)
        var textBal = view.findViewById<CustomTextViewNormal>(R.id.textBal)
        var imgCoin = view.findViewById<ImageView>(R.id.imgCoin)
        var consParent = view.findViewById<ConstraintLayout>(R.id.consParent)


    }
}