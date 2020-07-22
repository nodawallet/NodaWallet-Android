package com.application.nodawallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.nodawallet.R
import com.google.android.material.chip.Chip

class ShowPatternHomeAdapter (val context: Context, var list: List<String>) : RecyclerView.Adapter<ShowPatternHomeAdapter.ViewHolder>() {

    var onItemClick: ((pos: Int, view: View) -> Unit)? = null
    var count = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowPatternHomeAdapter.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pattern, parent, false)
        return ShowPatternHomeAdapter.ViewHolder(view)
    }


    override fun getItemCount(): Int {

        return list.size

    }

    override fun onBindViewHolder(holder: ShowPatternHomeAdapter.ViewHolder, position: Int) {

        count++

        holder.chipPattern.text = "$count."+list[position]

        holder.chipPattern.setOnClickListener {
            onItemClick?.invoke(position,it)
        }



    }

    fun deleteItem(list: List<String>){
        this.list = list
        notifyDataSetChanged()

    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var chipPattern =  itemView.findViewById<Chip>(R.id.chipPattern)


    }
}