package com.habiba.newsapp.fragments

import CategoryRecyclerAdapter
import NewsRecyclerView
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.habiba.newsapp.R
import com.habiba.newsapp.categoryData.Companion.categoryList
import com.habiba.newsapp.countries.Companion.countryMap
import com.habiba.newsapp.repository.repository
import com.habiba.newsapp.viewmodel.NewsViewModel
import com.habiba.newsapp.viewmodel.NewsViewModelFactory

class home : Fragment() {

    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryRecyclerAdapter
    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var newsAdapter: NewsRecyclerView
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var bannerImage: ImageView
    private lateinit var bannerHeader: TextView
    private lateinit var bannerSource: TextView
    private lateinit var spinner: Spinner

    private var selectedCategory: String? = null
    private var selectedCountry: String = "us" // Default country

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setupUI(view) // Organize initialization in one function

        // Initialize ViewModel
        val newsRepository = repository()
        val factory = NewsViewModelFactory(newsRepository)
        newsViewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]

        observeNewsData()
        observeRandomNews()

        // 🔹 Always fetch news after setting up UI
        fetchNews()

        return view
    }

    private fun setupUI(view: View) {
        bannerImage = view.findViewById(R.id.bannerImage)
        bannerSource = view.findViewById(R.id.bannerSource)
        bannerHeader = view.findViewById(R.id.bannerheadline)
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerview)
        newsRecyclerView = view.findViewById(R.id.newsRecyclerview)
        spinner = view.findViewById(R.id.customSpinner)

        setupCategoryRecyclerView()
        setupCountrySpinner()
        setupNewsRecyclerView()
    }

    private fun setupCategoryRecyclerView() {
        categoryRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoryAdapter = CategoryRecyclerAdapter(categoryList) { category ->
            selectedCategory = if (category == selectedCategory) null else category // Toggle selection
            fetchNews()
        }
        categoryRecyclerView.adapter = categoryAdapter
    }

    private fun setupCountrySpinner() {
        val countryList = countryMap.values.toList()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, countryList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setupNewsRecyclerView() {
        newsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        newsAdapter = NewsRecyclerView(emptyList(), selectedCountry, selectedCategory)
        newsRecyclerView.adapter = newsAdapter

    }

    private fun observeNewsData() {
        newsViewModel.newsLiveData.observe(viewLifecycleOwner) { newsList ->
            val articles = newsList ?: emptyList()

            Log.d("HomeFragment", "Articles received: ${articles.size}")

            if (articles.isEmpty()) {
                Log.w("HomeFragment", "Warning: No articles received. RecyclerView might be empty.")
            }

            newsAdapter.updateNews(articles)
        }
    }


    private fun observeRandomNews() {
        newsViewModel.randomNewsLiveData.observe(viewLifecycleOwner, Observer { article ->
            article?.let {
                bannerSource.text = it.source?.name ?: "Unknown Source"
                bannerHeader.text = it.title ?: "No Title"
                Glide.with(requireContext()).load(it.urlToImage).into(bannerImage)
            }
        })
    }

    private fun fetchNews() {
        Log.d("HomeFragment", "Fetching news for Category: $selectedCategory, Country: $selectedCountry")

        if (selectedCategory == null) {
            newsViewModel.fetchNewsCountryonly(selectedCountry)
        } else {
            newsViewModel.fetchNews(selectedCategory, selectedCountry)
        }
    }
}
