package com.habiba.newsapp.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.habiba.newsapp.constants
import com.habiba.newsapp.repository.repository
import com.habiba.newsapp.responce.Article
import com.habiba.newsapp.responce.SourceResponse
import com.habiba.newsapp.responce.SourceX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class NewsViewModel(private val repository: repository) : ViewModel() {

    private val _newsLiveData = MutableLiveData<List<Article>?>()
    val newsLiveData: LiveData<List<Article>?> = _newsLiveData

    private val _randomNewsLiveData = MutableLiveData<Article?>()
    val randomNewsLiveData: LiveData<Article?> = _randomNewsLiveData

    private val _sourceLiveData=MutableLiveData<List<SourceX?>>()
    val sourceLiveData:LiveData<List<SourceX?>> = _sourceLiveData

    fun randomNewsGenerator(articlesList: List<Article?>): Article? {
        if (articlesList.isEmpty()) return null
        val index = Random.nextInt(articlesList.size)
        return articlesList[index]
    }

     fun fetchSource() {
        Log.d("SourceNewsViewModel", "Fetching source")
        val apiUrl = "https://newsapi.org/v2/top-headlines/sources?apiKey=${constants.API_KEY}"
        Log.d("SourceNewsViewModel", "API Request URL: $apiUrl")
        repository.getSource().enqueue(object : Callback<SourceResponse> {

            override fun onResponse(call: Call<SourceResponse>, response: Response<SourceResponse>) {
                if (response.isSuccessful) {
                    val sources = response.body()?.sources ?: emptyList()

                    Log.d("SourceNewsViewModel", "Full API Response: ${response.body()?.toString()}")
                    Log.d("SourceNewsViewModel", "API Success - Sources Received: ${sources.size}")

                    _sourceLiveData.postValue(sources)
                } else {
                    Log.e("SourceNewsViewModel", "API Response Error - Code: ${response.code()}, Message: ${response.errorBody()?.string()}")
                    _sourceLiveData.postValue(emptyList())
                }
            }

            override fun onFailure(call: Call<SourceResponse>, t: Throwable) {
                Log.e("SourceNewsViewModel", "API Call Failed: ${t.message}", t)
                _sourceLiveData.postValue(emptyList())
            }
        })
    }

    fun fetchNewsBySources(sourceIds: List<String>) {
        viewModelScope.launch {
            try {
                val response = repository.getNewsBySources(sourceIds)
                if (response.isSuccessful && response.body() != null) {
                    Log.d("NewsViewModel", "Fetching news for Category: , Country:")
                    _newsLiveData.postValue(response.body()?.articles ?: emptyList())
                } else {
                    _newsLiveData.postValue(emptyList()) // Handle failure case
                }
            } catch (e: Exception) {
                _newsLiveData.postValue(emptyList()) // Handle exception
            }
        }
    }

    suspend fun fetchNewsByCountryandCategory(category: String?, country: String?) {
        Log.d("newsViewModel", "Fetching news for category=$category & country=$country")

        withContext(Dispatchers.IO) { // Ensure network calls run in the background
            try {
                val call = repository.getHeadlines(country, category) // Still a Call<SourceResponse>
                val response = call.execute() //  Convert it to a synchronous call

                if (response.isSuccessful) {
                    val articles = response.body()?.sources ?: emptyList()
                    Log.d("NewsViewModel", "API Success - Articles Received: ${articles.size}")
                    _sourceLiveData.postValue(articles)
                } else {
                    Log.e("NewsViewModel", "API Response Error - Code: ${response.code()}, Message: ${response.errorBody()?.string()}")
                    _sourceLiveData.postValue(emptyList())
                }
            } catch (e: Exception) {
                Log.e("NewsViewModel", "API Call Failed: ${e.message}", e)
                _sourceLiveData.postValue(emptyList())
            }
        }
    }

     fun fetchNewsByCountryonly(country: String?) {

        repository.getHeadlinesCountry(country).enqueue(object : Callback<SourceResponse> {
            override fun onResponse(call: Call<SourceResponse>, response: Response<SourceResponse>) {
                if (response.isSuccessful) {
                    val articles = response.body()?.sources ?: emptyList()
                    Log.d("NewsViewModel", "API Success - Articles Fetched: ${articles.size}")
                    _sourceLiveData.postValue(articles)

                    // Assign a random article
//                    _randomNewsLiveData.postValue(randomNewsGenerator(articles))
                } else {
                    Log.e("NewsViewModel", "API Response Error - Code: ${response.code()}, Message: ${response.errorBody()?.string()}")
                    _sourceLiveData.postValue(emptyList())
                }
            }

            override fun onFailure(call: Call<SourceResponse>, t: Throwable) {
                Log.e("NewsViewModel", "API Call Failed: ${t.message}", t)
                _sourceLiveData.postValue(emptyList())
            }
        })
    }


    suspend fun fetchNewsByCategoryOnly(category:String){
        withContext(Dispatchers.IO) { // Ensure network calls run in the background
            try {
                val call = repository.getNewsByCategoryOnly(category,80) // Still a Call<SourceResponse>
                val response = call.execute() //  Convert it to a synchronous call

                if (response.isSuccessful) {
                    val articles = response.body()?.sources ?: emptyList()
                    Log.d("NewsViewModel333", "API Success - Articles Received: ${articles.size}")
                    _sourceLiveData.postValue(articles)
                } else {
                    Log.e("NewsViewModel", "API Response Error - Code: ${response.code()}, Message: ${response.errorBody()?.string()}")
                    _sourceLiveData.postValue(emptyList())
                }
            } catch (e: Exception) {
                Log.e("NewsViewModel", "API Call Failed: ${e.message}", e)
                _sourceLiveData.postValue(emptyList())
            }
        }

    }




}
