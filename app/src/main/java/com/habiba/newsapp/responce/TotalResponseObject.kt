package com.habiba.newsapp.responce

data class TotalResponseObject(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)