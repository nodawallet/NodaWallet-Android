package com.application.nodawallet.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.nodawallet.R
import com.application.nodawallet.model.CoinList
import com.application.nodawallet.model.ExchangeHistoryList
import com.application.nodawallet.model.History
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.CustomTextViewNormal
import com.application.nodawallet.utils.CustomTextViewSemiBold
import com.application.nodawallet.utils.UtilsDefault
import kotlinx.android.synthetic.main.activity_trans_details.*
import okio.Utf8
import java.text.SimpleDateFormat
import java.util.*

class ExchangeAdapter  (val context: Context) : RecyclerView.Adapter<ExchangeAdapter.ViewHolder>() {

    private var list = emptyList<ExchangeHistoryList>()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeAdapter.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exchange, parent, false)
        return ExchangeAdapter.ViewHolder(view)
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ExchangeAdapter.ViewHolder, position: Int) {
        val model:ExchangeHistoryList = list[position]
        try {
            holder.textPair.text = model.spend_currency+"/"+model.receive_currency
            holder.textFees.text = UtilsDefault.formatCryptoCurrency(model.fee.toString())+" "+model.spend_currency
            holder.textSendAmount.text = ""+UtilsDefault.formatCryptoCurrency(model.spend_amount.toString())+" "+model.spend_currency
            holder.textReceiveAmount.text = ""+UtilsDefault.formatCryptoCurrency(model.receive_amounty.toString())+" "+model.receive_currency
            holder.textStatus.text = model.status.toString()
            holder.textTransHash.text = model.tax_id
            holder.textTime.text = model.date
        }
        catch (e:Exception){
            e.printStackTrace()
        }

        holder.textTransHash.setOnClickListener {
            val uri: Uri = Uri.parse(Constants.ETHERSCAN_TX+model.tax_id)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        }

    }

    internal fun setlist(words: List<ExchangeHistoryList>) {
        this.list = words
        notifyDataSetChanged()
    }



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textTime = itemView.findViewById<CustomTextViewSemiBold>(R.id.textTime)
        var textBuySell = itemView.findViewById<CustomTextViewSemiBold>(R.id.textBuySell)
        var textPair = itemView.findViewById<CustomTextViewNormal>(R.id.textPair)
        var textSendAmount = itemView.findViewById<CustomTextViewNormal>(R.id.textSendAmount)
        var textReceiveAmount = itemView.findViewById<CustomTextViewNormal>(R.id.textReceiveAmount)
        var textFees = itemView.findViewById<CustomTextViewNormal>(R.id.textFees)
        var textStatus = itemView.findViewById<CustomTextViewNormal>(R.id.textStatus)
        var textTransHash = itemView.findViewById<CustomTextViewNormal>(R.id.textTransHash)


    }

}