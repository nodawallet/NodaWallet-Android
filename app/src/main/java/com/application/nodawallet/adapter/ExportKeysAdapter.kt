package com.application.nodawallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.recyclerview.widget.RecyclerView
import com.application.nodawallet.R
import com.application.nodawallet.model.CoinList
import com.application.nodawallet.utils.CustomTextViewNormal
import com.application.nodawallet.utils.CustomTextViewSemiBold

class ExportKeysAdapter(val context: Context) :
    RecyclerView.Adapter<ExportKeysAdapter.ViewHolder>() {

    private var list = emptyList<CoinList>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExportKeysAdapter.ViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_exportkeys, parent, false)
        return ExportKeysAdapter.ViewHolder(view)
    }


    override fun getItemCount(): Int {

        return list.size

    }

    override fun onBindViewHolder(holder: ExportKeysAdapter.ViewHolder, position: Int) {

        val model = list[position]
        holder.textCoin.text = model.coinName
        holder.textPublicKey.text = model.publicKey


    }

    internal fun setCoins(words: List<CoinList>) {
        this.list = words
        notifyDataSetChanged()
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textCoin = itemView.findViewById<CustomTextViewSemiBold>(R.id.textCoin)
        val textPublicKey = itemView.findViewById<CustomTextViewNormal>(R.id.textPublicKey)

    }

}