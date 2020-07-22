package com.application.nodawallet.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.nodawallet.R
import com.application.nodawallet.activity.MainActivity
import com.application.nodawallet.fragment.HomeFragment
import com.application.nodawallet.fragment.SettingsFragment
import com.application.nodawallet.fragment.TokenFragment
import com.application.nodawallet.model.currencyModel
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.UtilsDefault


class CurrencyAdapter(private val context: Context, private val list:ArrayList<currencyModel>) : RecyclerView.Adapter<CurrencyAdapter.SectionViewHolder2>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int): CurrencyAdapter.SectionViewHolder2 {

        val view = LayoutInflater.from(context).inflate(R.layout.section_list, parent, false)
        return SectionViewHolder2(view)

    }

    override fun getItemCount(): Int {

        return list.size

    }

    override fun onBindViewHolder(holder: CurrencyAdapter.SectionViewHolder2, position: Int) {

        val model:currencyModel = list[position]
        holder.radiobutton.text = model.symbol+"-"+model.name
        holder.imgFlag.setImageResource(model.coinImage)

        var mycurrency = UtilsDefault.getSharedPreferenceString(Constants.CURRENCY)
        if (mycurrency == null){
            UtilsDefault.updateSharedPreferenceString(Constants.CURRENCY,"USD-$")
            mycurrency = "USD-$"

        }
        val currency = model.symbol
        val curr = mycurrency.split("-")

        if (currency == curr[0]){
            holder.radiobutton.isChecked = true
        }

        holder.radiobutton.setOnCheckedChangeListener { buttonView, isChecked ->
            UtilsDefault.updateSharedPreferenceString(Constants.CURRENCY,model.symbol+"-"+model.coinsymbol)
            (context as MainActivity).push(HomeFragment())

        }
    }

    inner class SectionViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var radiobutton:RadioButton=itemView.findViewById(R.id.radio_button1)
        var imgFlag:ImageView=itemView.findViewById(R.id.imgFlag)

    }
}



