package com.application.nodawallet.adapter

import android.content.Context
import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.application.nodawallet.R
import com.application.nodawallet.activity.NewsDetailActivity
import com.application.nodawallet.model.History
import com.application.nodawallet.model.NewsList
import com.application.nodawallet.utils.CustomTextViewNormal
import com.application.nodawallet.utils.CustomTextViewSemiBold
import com.squareup.picasso.Picasso

class NewsAdapter (val context: Context) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    private var list = emptyList<NewsList>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdapter.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return list.size
    }

    override fun onBindViewHolder(holder: NewsAdapter.ViewHolder, position: Int) {

        val model:NewsList = list[position]
        holder.textTitle.text = model.heading
        holder.textDate.text = model.date
        holder.textTime.text = model.time
        holder.textContent.text = Html.fromHtml(model.content)

        if (model.image!= ""){
            Picasso.with(context).load(model.image).into(holder.imgNews)
        }

        holder.rlParent.setOnClickListener {
            val intent = Intent(context,NewsDetailActivity::class.java)
            intent.putExtra("title",model.heading)
            intent.putExtra("date",model.date)
            intent.putExtra("time",model.time)
            intent.putExtra("content",model.content)
            intent.putExtra("image",model.image)
            context.startActivity(intent)
        }

    }

    internal fun setnews(words: List<NewsList>) {
        this.list = words
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imgNews = itemView.findViewById<ImageView>(R.id.imgNews)
        var textTitle = itemView.findViewById<CustomTextViewSemiBold>(R.id.textTitle)
        var textTime = itemView.findViewById<CustomTextViewNormal>(R.id.textTime)
        var textDate = itemView.findViewById<CustomTextViewNormal>(R.id.textDateNews)
        var textContent = itemView.findViewById<CustomTextViewNormal>(R.id.textContent)
        var rlParent = itemView.findViewById<RelativeLayout>(R.id.rlParent)


    }
}