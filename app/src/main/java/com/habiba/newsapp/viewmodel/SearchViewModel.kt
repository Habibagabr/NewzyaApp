package com.habiba.newsapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habiba.newsapp.repository.repository
import com.habiba.newsapp.responce.Article
import kotlinx.coroutines.launch

class SearchViewModel(private val repo : repository):ViewModel() {

    private val _searchResults = MutableLiveData<List<Article>?>()
    val searchResults: LiveData<List<Article>?> = _searchResults

    fun searchNews(query: String) {
        if (query.isBlank()) return // 🔹 Avoid unnecessary API calls for empty queries

        viewModelScope.launch {
            val response = repo.getSearch(query)
            if (response.isSuccessful) {
                _searchResults.postValue(response.body()?.articles ?: emptyList())
            } else {
                _searchResults.postValue(emptyList())
            }
        }
    }

}