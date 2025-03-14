package com.habiba.newsapp.APIResquests
import com.habiba.newsapp.constants
import com.habiba.newsapp.responce.Article
import com.habiba.newsapp.responce.SourceResponse
import com.habiba.newsapp.responce.TotalResponseObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/*
* why we make interface ?
*          " interface = a contract that defines how API calls should be made by defining the endpoints"
*          " retrofit object = uses this interface to generate the actual implementation at runtime. "
*
* 1) here we write the functions and the methods we need this functions done by ex."get / post /delete/.."
* 2) above we write the method we need the function done by
* 3) the function parameter - parameter from our own code " doesn't included in the URL "
*                           - parameters included in the URL but we will passing it from our side
* 4) the second type of the parameter has two sides : in the URL and from our side so we use Query annotation
* 5) the query parameter is the same as the URL parameter
* 6) then we passing this functions to the retrofit object which is responsible  for implementing this requests*/

interface API_Interface {
    @GET("v2/top-headlines")
    suspend fun getHeadlines(
        @Query("country")
        countryCode:String?=null,
        @Query("category")
        category:String?=null,
        @Query("apiKey")
        apiKey:String=constants.API_KEY
    ): Response<TotalResponseObject>

    @GET("v2/top-headlines")
    suspend fun getHeadlinesByCountryOnly(
        @Query("country")
        countryCode: String?,
        @Query("apiKey")
        apiKey:String=constants.API_KEY
    ): Response<TotalResponseObject>


    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        keyword:String,
        @Query("apiKey")
        apiKey:String=constants.API_KEY
    )
}