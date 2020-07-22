package com.application.nodawallet.adapter

import android.os.Build
import android.text.Html
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.application.nodawallet.R
import com.application.nodawallet.model.FAQResponse
import com.application.nodawallet.model.FaqList
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.UtilsDefault
import kotlinx.android.synthetic.main.activity_news_detail.*
import kotlinx.android.synthetic.main.adapter_faq.view.*
import okio.Utf8

class FAQAdapter (var walletList: List<FaqList>, var context: FragmentActivity, var type: Int) :
    RecyclerView.Adapter<FAQAdapter.viewHolder>() {

    var isAnsShown = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_faq, parent, false)

        return viewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return walletList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        val model:FaqList = walletList[position]

        if (UtilsDefault.getSharedPreferenceString(Constants.LANGUAGE) == "en"){
            holder.question.text =model.question
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.answer?.text = Html.fromHtml(model.answer, Html.FROM_HTML_MODE_LEGACY)

            } else {
                holder.answer?.text = Html.fromHtml(model.answer)

            }
            Linkify.addLinks(holder.answer, Linkify.ALL)

        }
        else {
            holder.question.text =model.question_rs
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.answer?.text = Html.fromHtml(model.answer_rs, Html.FROM_HTML_MODE_LEGACY)

            } else {
                holder.answer?.text = Html.fromHtml(model.answer_rs)

            }
            Linkify.addLinks(holder.answer, Linkify.ALL)

        }


        holder.faqChildConst.setOnClickListener {
            if (!isAnsShown) {
                isAnsShown = true
                //itemView.answer.maxLines = 100
                holder.expandIcon.setText("-")
                holder.answer.visibility=View.VISIBLE

            } else {
                isAnsShown = false
                //itemView.answer.maxLines = 2
                holder.expandIcon.setText("+")
                holder.answer.visibility=View.GONE

            }
        }


    }

    inner class viewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var faqChildConst = view.findViewById<LinearLayout>(R.id.faqChildConst)
        var question = view.findViewById<TextView>(R.id.question)
        var answer = view.findViewById<TextView>(R.id.answer)
        var expandIcon = view.findViewById<TextView>(R.id.expandIcon)

    }
}