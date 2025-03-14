package com.habiba.newsapp.fragments

import CategoryRecyclerAdapter
import NewsRecyclerView
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.habiba.newsapp.R
import com.habiba.newsapp.categoryData.Companion.categoryList
import com.habiba.newsapp.countries.Companion.countryMap
import com.habiba.newsapp.repository.repository
import com.habiba.newsapp.responce.SourceX
import com.habiba.newsapp.viewmodel.NewsViewModel
import com.habiba.newsapp.viewmodel.NewsViewModelFactory
import kotlinx.coroutines.launch

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
    private var selectedCountry: String ?= null // Default country
    private var sourceList: List<SourceX?> = emptyList()


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

        observeSourceData()
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
            fetchNews() // 🔹 Call fetchNews() to update RecyclerView
        }

        categoryRecyclerView.adapter = categoryAdapter
    }


    private fun setupCountrySpinner() {
        val countryList = countryMap.values.toList()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, countryList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCountry = countryMap.keys.toList()[position] // Get the selected country key
                fetchNews() // 🔹 Fetch news whenever the country changes
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }


    private fun setupNewsRecyclerView() {
        newsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        newsAdapter = NewsRecyclerView(emptyList(), mutableListOf())
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

            // ✅ Update banner with first article
            if (articles.isNotEmpty()) {
                val randomArticle = newsViewModel.randomNewsGenerator(articles)
                randomArticle?.let {
                    Glide.with(requireContext()).load(it.urlToImage).into(bannerImage)
                    bannerSource.text = it.source.name ?: "Unknown Source"
                    bannerHeader.text = it.title ?: "No Title"
                }
            }
        }
    }



    private fun observeSourceData() {
        newsViewModel.sourceLiveData.observe(viewLifecycleOwner) { newsList ->
            sourceList = newsList ?: emptyList()

            Log.d("HomeFragment", "Sources received: ${sourceList.size}")

            if (sourceList.isEmpty()) {
                Log.w("HomeFragment", "Warning: No sources received.")
            } else {
                val sourceIds = sourceList.mapNotNull { it?.id }

                if (sourceIds.isNotEmpty()) {
                    Log.d("HomeFragment", "Fetching news for sources: $sourceIds")
                    newsViewModel.fetchNewsBySources(sourceIds)
                } else {
                    Log.w("HomeFragment", "No valid source IDs found. Skipping news fetch.")
                }
            }

            // ✅ Update the sources in the adapter when new sources arrive
            newsAdapter.updateSources(sourceList.filterNotNull())
        }
    }

    private fun fetchNews() {
        Log.d("HomeFragment", "Fetching news for Category: $selectedCategory, Country: $selectedCountry")

        viewLifecycleOwner.lifecycleScope.launch {
            when {
                !selectedCategory.isNullOrEmpty() && !selectedCountry.isNullOrEmpty() -> {
                    // ✅ Fetch news filtered by both category and country
                    newsViewModel.fetchNewsByCountryandCategory(selectedCategory, selectedCountry)
                }
                !selectedCategory.isNullOrEmpty() -> {
                    // ✅ Fetch news filtered by category only
                    newsViewModel.fetchNewsByCategoryOnly(selectedCategory!!)
                }
                !selectedCountry.isNullOrEmpty() -> {
                    // ✅ Fetch news filtered by country only
                    newsViewModel.fetchNewsByCountryonly(selectedCountry)
                }
                else -> {
                    // ✅ If no filters are selected, fetch general news
                    newsViewModel.fetchSource()
                }
            }
        }
    }



    private fun observeRandomNews() {
        newsViewModel.randomNewsLiveData.observe(viewLifecycleOwner, Observer { article ->
            article?.let {
                bannerSource.text = it.source.name ?: "Unknown Source"
                bannerHeader.text = it.title ?: "No Title"
                Glide.with(requireContext()).load(it.urlToImage).into(bannerImage)
            }
        })
    }



}
