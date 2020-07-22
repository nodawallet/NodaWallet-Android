package com.application.nodawallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.nodawallet.R
import com.application.nodawallet.model.CoinList
import com.application.nodawallet.utils.CustomTextViewNormal
import com.squareup.picasso.Picasso
import com.suke.widget.SwitchButton

class AddTokenAdapter (val context: Context) : RecyclerView.Adapter<AddTokenAdapter.ViewHolder>() {

    private var list = emptyList<CoinList>()

    var onItemClick: ((pos: Int, view: View) -> Unit)? = null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddTokenAdapter.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_add_token, parent, false)
        return AddTokenAdapter.ViewHolder(view)
    }


    override fun getItemCount(): Int {

        return list.size

    }

    override fun onBindViewHolder(holder: AddTokenAdapter.ViewHolder, position: Int) {

        val model = list[position]

        holder.mToggle.visibility =  View.VISIBLE

        holder.mToggle.isChecked = model.enableStatus == 1

        holder.textTokenName.text = model.coinName

        try {
            Picasso.with(context).load(model.tokenImage).into(holder.imgBtc)
        }
        catch (e:Exception){
            e.printStackTrace()
        }




        holder.mToggle.setOnCheckedChangeListener { view, isChecked ->

            if (view.isPressed){

                onItemClick?.invoke(position,view)

            }
        }



    }

    internal fun setCoins(words: List<CoinList>) {
        this.list = words
        notifyDataSetChanged()
    }



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mToggle: Switch = itemView.findViewById(R.id.mToggle)
        val textTokenName = itemView.findViewById<CustomTextViewNormal>(R.id.textTokenName)
        val imgBtc = itemView.findViewById<ImageView>(R.id.imgBtc)

    }

}