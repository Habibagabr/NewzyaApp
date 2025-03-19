package com.habiba.newsapp.repository

import android.util.Log
import com.habiba.newsapp.APIResquests.retrofit
import com.habiba.newsapp.constants
import com.habiba.newsapp.responce.Article
import com.habiba.newsapp.responce.SourceResponse
import com.habiba.newsapp.responce.TotalResponseObject
import retrofit2.Call
import retrofit2.Response

class repository {
     fun getHeadlines(country: String?, category: String?):  Call<SourceResponse>  {
        Log.d("NewsRepository", "API Request - Category: $category, Country: $country, API Key: ${constants.API_KEY}")

        return retrofit.api.getHeadlines(country, category, 80,constants.API_KEY) //  Correct reference
    }

     fun getHeadlinesCountry(country:String?): Call<SourceResponse> {
        return retrofit.api.getHeadlinesByCountryOnly(country,80,constants.API_KEY) //  Correct reference
    }

     fun getSource(): Call<SourceResponse> {
        return retrofit.api.getNewsSources(80,constants.API_KEY)
    }

    suspend fun getNewsBySources(sourceIds: List<String>): Response<TotalResponseObject> {
        val sourcesParam = sourceIds.joinToString(",") // Convert list to a comma-separated string
        return retrofit.api.getNewsBySources(sourcesParam,80)
    }
     fun getNewsByCategoryOnly(category:String,pageSize:Int):Call<SourceResponse>{
        return retrofit.api.getHeadlinesByCategoryOnly(category,pageSize,constants.API_KEY)

    }
    suspend fun getSearch(query:String):Response<TotalResponseObject>{
        return retrofit.api.searchForNews(query,80,constants.API_KEY)

    }
}
