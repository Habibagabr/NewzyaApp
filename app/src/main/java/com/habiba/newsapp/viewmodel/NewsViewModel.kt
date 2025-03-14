package com.habiba.newsapp.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.habiba.newsapp.repository.repository
import com.habiba.newsapp.responce.Article
import kotlinx.coroutines.launch
import kotlin.random.Random

class NewsViewModel(private val repository: repository) : ViewModel() {

    private val _newsLiveData = MutableLiveData<List<Article>?>()
    val newsLiveData: LiveData<List<Article>?> = _newsLiveData

    private val _randomNewsLiveData = MutableLiveData<Article?>()
    val randomNewsLiveData: LiveData<Article?> = _randomNewsLiveData

    fun fetchNews(category: String?, country: String?) {
        viewModelScope.launch {
            try {
                Log.d("NewsViewModel", "Fetching news for Category: $category, Country: $country")

                val response = repository.getHeadlines(country, category)

                if (response.isSuccessful) {
                    val articles = response.body()?.articles ?: emptyList()

                    // 🔹 Log full response to check why it's empty
                    Log.d("NewsViewModel", "Full API Response: ${response.body()?.toString()}")
                    Log.d("NewsViewModel", "API Success - Articles Received: ${articles.size}")

                    _newsLiveData.postValue(articles)
                } else {
                    Log.e("NewsViewModel", "API Response Error - Code: ${response.code()}, Message: ${response.errorBody()?.string()}")
                    _newsLiveData.postValue(emptyList())
                }
            } catch (e: Exception) {
                Log.e("NewsViewModel", "API Call Failed: ${e.message}", e)
                _newsLiveData.postValue(emptyList())
            }
        }
    }


    fun fetchNewsCountryonly(country: String?) {
        viewModelScope.launch {
            try {
                val response = repository.getHeadlinesCountry(country)

                if (response.isSuccessful) {
                    val articles = response.body()?.articles ?: emptyList()
                    Log.d("NewsViewModel", "API Success - Articles Fetched: ${articles.size}")
                    _newsLiveData.postValue(articles)

                    // Correctly assign a random article
                    _randomNewsLiveData.postValue(randomNewsGenerator(articles))

                } else {
                    Log.e("NewsViewModel", "API Response Error - Code: ${response.code()}, Message: ${response.errorBody()?.string()}")
                    _newsLiveData.postValue(emptyList())
                }
            } catch (e: Exception) {
                Log.e("NewsViewModel", "API Call Failed: ${e.message}", e)
                _newsLiveData.postValue(emptyList())
            }
        }
    }

    fun randomNewsGenerator(articlesList: List<Article?>): Article? {
        if (articlesList.isEmpty()) return null
        val index = Random.nextInt(articlesList.size)
        return articlesList[index]
    }
}
