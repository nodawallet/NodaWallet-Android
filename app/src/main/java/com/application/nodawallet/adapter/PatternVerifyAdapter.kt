package com.application.nodawallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.nodawallet.R
import com.google.android.material.chip.Chip

class PatternVerifyAdapter (val context: Context, var list: List<String>) : RecyclerView.Adapter<PatternVerifyAdapter.ViewHolder>() {


    var onItemClick: ((pos: Int, view: View) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatternVerifyAdapter.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pattern, parent, false)
        return PatternVerifyAdapter.ViewHolder(view)
    }


    override fun getItemCount(): Int {

        return list.size

    }

    override fun onBindViewHolder(holder: PatternVerifyAdapter.ViewHolder, position: Int) {

        holder.chipPattern1.text = list[position]

        holder.chipPattern1.setOnClickListener {
            onItemClick?.invoke(position,it)
        }

    }


    fun deleteItem(list: List<String>){
        this.list = list
        notifyDataSetChanged()

    }



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var chipPattern1 =  itemView.findViewById<Chip>(R.id.chipPattern)


    }
}