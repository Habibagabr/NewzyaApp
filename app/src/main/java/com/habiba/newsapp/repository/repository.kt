package com.habiba.newsapp.repository

import android.util.Log
import com.habiba.newsapp.APIResquests.retrofit
import com.habiba.newsapp.constants
import com.habiba.newsapp.responce.Article
import com.habiba.newsapp.responce.SourceResponse
import com.habiba.newsapp.responce.TotalResponseObject
import retrofit2.Response

class repository {
    suspend fun getHeadlines(country: String?, category: String?): Response<TotalResponseObject> {
        Log.d("NewsRepository", "API Request - Category: $category, Country: $country, API Key: ${constants.API_KEY}")

        return retrofit.api.getHeadlines(country, category, constants.API_KEY) // ✅ Correct reference
    }

    suspend fun getHeadlinesCountry(country:String?):Response<TotalResponseObject>{
        return retrofit.api.getHeadlinesByCountryOnly(country,constants.API_KEY) // ✅ Correct reference
    }


}
