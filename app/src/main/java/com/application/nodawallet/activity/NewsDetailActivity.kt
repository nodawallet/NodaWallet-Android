package com.application.nodawallet.activity

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import com.application.nodawallet.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_news_detail.*

class NewsDetailActivity : BaseActivity() {

    var title = ""
    var content = ""
    var date = ""
    var time = ""
    var image = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)


        if (intent != null) {
            title = intent.getStringExtra("title") ?: ""
            title = intent.getStringExtra("title") ?: ""
            date = intent.getStringExtra("date") ?: ""
            time = intent.getStringExtra("time") ?: ""
            content = intent.getStringExtra("content") ?: ""
            image = intent.getStringExtra("image") ?: ""

        }

        textTimeDetail?.text = time
        textDateDetail?.text = date
        textTitleDetail?.text = title
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textContentDetail?.text = Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY)

        } else {
            textContentDetail?.text = Html.fromHtml(content)

        }
        Linkify.addLinks(textContentDetail,Linkify.ALL)

        if (image!= ""){
            Picasso.with(this).load(image).into(imgNewsDetail)
        }

        imgBackNews.setOnClickListener {
            onBackPressed()
        }


    }
}
