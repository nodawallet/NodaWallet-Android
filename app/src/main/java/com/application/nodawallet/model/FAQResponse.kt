package com.application.nodawallet.model

data class FAQResponse(val status: Boolean?, val message: List<FaqList>?)

data class FaqList(val question: String?, val answer: String?, val question_rs:String?,val answer_rs:String?)
