package com.application.nodawallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Switch
import androidx.recyclerview.widget.RecyclerView
import com.application.nodawallet.R
import com.application.nodawallet.model.ImportTokenModel
import com.application.nodawallet.utils.CustomTextViewNormal
import com.suke.widget.SwitchButton

class NetworkTokenAdapter (val context: Context,val list:ArrayList<ImportTokenModel>) : RecyclerView.Adapter<NetworkTokenAdapter.ViewHolder>() {

    var onItemClick: ((pos: Int, view: View) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NetworkTokenAdapter.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_add_token, parent, false)
        return NetworkTokenAdapter.ViewHolder(view)
    }


    override fun getItemCount(): Int {

        return list.size

    }

    override fun onBindViewHolder(holder: NetworkTokenAdapter.ViewHolder, position: Int) {

        val model = list[position]

        holder.textTokenName.text =  model.tokenName
        holder.mToggle.visibility = View.INVISIBLE
        holder.LinearLayout1.setOnClickListener {
            onItemClick?.invoke(position,it)

        }

    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var mToggle: Switch = itemView.findViewById(R.id.mToggle)
        val textTokenName = itemView.findViewById<CustomTextViewNormal>(R.id.textTokenName)
        val LinearLayout1 = itemView.findViewById<RelativeLayout>(R.id.LinearLayout1)

    }

}