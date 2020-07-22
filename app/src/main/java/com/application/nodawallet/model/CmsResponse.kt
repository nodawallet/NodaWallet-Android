package com.application.nodawallet.model


data class CmsResponse(val status: Boolean?, val message: List<CmsList>?)

data class CmsList(val title: String?, val title_rs: String?, val description: String?,
                             val description_rs: String?, val position: Number?, val _id: String?,
                             val page_key: String?, val created_at: String?, val modified_at: String?)

