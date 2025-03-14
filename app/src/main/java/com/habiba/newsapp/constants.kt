package com.habiba.newsapp

class constants {

    //Companion object let us access the variables / methods without taking an object from the class " same as static in java"
    companion object{
        const val API_KEY="efde5d5f44af460c9cfc652553045fc3"
        const val BASE_URL="https://newsapi.org"
        //between each search request and other = 500ms = 0.5sec
        const val SEARCH_NEWS_TIME_DELAY="500L"
        //Limits put to not bring all the data at once so instead we split the data into pages per request
        //when loading all at once it consume more and more time and memory
        //split data into pages per request called "paging"
        const val QUERY_PAGE_SIZE="20"



    }
}