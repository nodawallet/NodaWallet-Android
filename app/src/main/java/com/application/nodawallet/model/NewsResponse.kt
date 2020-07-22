package com.application.nodawallet.model


data class NewsResponse(val status: Boolean?, val message: List<NewsList>?)

data class NewsList(val heading: String?, val content: String?, val image: String?, val date: String?, val time: String?, val _id: String?)
