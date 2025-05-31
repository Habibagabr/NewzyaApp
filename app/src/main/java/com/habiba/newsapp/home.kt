package com.habiba.newsapp

import CategoryRecyclerAdapter
import NewsRecyclerView
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
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
    private lateinit var frameCard: FrameLayout
    private lateinit var animtext: TextView


    private var selectedCategory: String? = null
    private var selectedCountry: String? = null // Default country
    private var sourceList: List<SourceX?> = emptyList()
    private lateinit var animation: LottieAnimationView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        animtext=view.findViewById(R.id.animtext)
        animtext.text="Loading , Please wait a minute"

        ////loading animation
        animation=view.findViewById(R.id.lottieAnimationView)
        animation.setAnimation(R.raw.loading)



        setupUI(view) // Organize initialization in one function

        // Initialize ViewModel
        val newsRepository = repository()
        val factory = NewsViewModelFactory(newsRepository)
        newsViewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]

        observeNewsData()
        observeSourceData()
        observeRandomNews()

        //  Always fetch news after setting up UI
        fetchNews()
        //  Set a click listener that updates dynamically later
        frameCard.setOnClickListener {
            val currentArticle = newsViewModel.randomNewsLiveData.value  // Get latest article
            if (!currentArticle?.url.isNullOrEmpty()) {
                Log.d("NewsFragment", " Opening URL: ${currentArticle?.url}")
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentArticle?.url))
                try {
                    requireContext().startActivity(intent)
                } catch (e: Exception) {
                    Log.e("NewsFragment", " Error opening URL: ${e.message}")
                }
            } else {
                Log.e("NewsFragment", " No URL found for this article")
            }
        }
        return view
    }

    private fun setupUI(view: View) {
        bannerImage = view.findViewById(R.id.bannerImage)
        bannerSource = view.findViewById(R.id.bannerSource)
        bannerHeader = view.findViewById(R.id.bannerheadline)
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerview)
        newsRecyclerView = view.findViewById(R.id.newsRecyclerview)
        spinner = view.findViewById(R.id.customSpinner)
        frameCard = view.findViewById(R.id.FrameCardHome)

        setupCategoryRecyclerView()
        setupCountrySpinner()
        setupNewsRecyclerView()
    }

    private fun setupCategoryRecyclerView() {
        categoryRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        categoryAdapter = CategoryRecyclerAdapter(categoryList) { category ->
            selectedCategory =
                if (category == selectedCategory) selectedCategory else category // Toggle selection
            fetchNews()
        }

        categoryRecyclerView.adapter = categoryAdapter
    }


    private fun setupCountrySpinner() {
        val countryList = countryMap.values.toList()
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, countryList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedCountry = countryMap.keys.toList()[position] // Get the selected country key
                fetchNews() //  Fetch news whenever the country changes
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }


    private fun setupNewsRecyclerView() {
        newsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        newsAdapter = NewsRecyclerView(
            emptyList(),
            mutableListOf(),
            countryMap,
            selectedCategory ?: "general"
        )
        newsRecyclerView.adapter = newsAdapter

    }

    private fun observeNewsData() {
        newsViewModel.newsLiveData.observe(viewLifecycleOwner) { newsList ->
            val articles = newsList ?: emptyList()

            Log.d("HomeFragment", "Articles received: ${articles.size}")

            if (articles.isEmpty()) {
                Log.w("HomeFragment", "Warning: No articles received. RecyclerView might be empty.")
                animation.setAnimation(R.raw.notfound)
                animation.repeatCount = LottieDrawable.INFINITE // Loop animation
                animation.speed = 0.5f
                animation.playAnimation()
                animtext.text="No news articles found for the selected filters"

                newsAdapter.updateNews(emptyList())

            }

            newsAdapter.updateNews(articles, selectedCategory ?: "general")

            // Update banner with first article
            if (articles.isNotEmpty()) {
                val randomArticle = newsViewModel.randomNewsGenerator(articles)
                randomArticle?.let {
                    Glide.with(requireContext()).load(it.urlToImage).into(bannerImage)
                    bannerSource.text = it.source.name
                    bannerHeader.text = it.title
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
                animation.setAnimation(R.raw.notfound)
                animation.repeatCount = LottieDrawable.INFINITE // Loop animation
                animation.speed = 0.5f
                animation.playAnimation()
                animtext.text="No news articles found for the selected filters"

                newsAdapter.updateSources(emptyList())
                newsAdapter.updateNews(emptyList())

            } else {
                val sourceIds = sourceList.mapNotNull { it?.id }

                if (sourceIds.isNotEmpty()) {
                    Log.d("HomeFragment", "Fetching news for sources: $sourceIds")
                    newsViewModel.fetchNewsBySources(sourceIds)
                } else {
                    animation.setAnimation(R.raw.notfound)
                    animation.repeatCount = LottieDrawable.INFINITE // Loop animation
                    animation.speed = 0.5f
                    animation.playAnimation()

                    Log.w("HomeFragment", "No valid source IDs found. Skipping news fetch.")
                }
            }

            // Update the sources in the adapter when new sources arrive
            newsAdapter.updateSources(sourceList)
        }
    }

    private fun fetchNews() {
        Log.d(
            "HomeFragment",
            "Fetching news for Category: $selectedCategory, Country: $selectedCountry"
        )

        viewLifecycleOwner.lifecycleScope.launch {
            when {
                !selectedCategory.isNullOrEmpty() && !selectedCountry.isNullOrEmpty() -> {
                    // Fetch news filtered by both category and country
                    Log.d(
                        "NewsFragment",
                        "we have category = {$selectedCategory} and country = {$selectedCountry}"
                    )
                    newsViewModel.fetchNewsByCountryandCategory(selectedCategory, selectedCountry)
                }

                !selectedCategory.isNullOrEmpty() && selectedCountry.isNullOrEmpty() -> {
                    //  Fetch news filtered by category only
                    Log.d("NewsFragment", "we only have category = {$selectedCategory} ")
                    Log.d("NewsFragment", "we  have country = {$selectedCountry} ")

                    newsViewModel.fetchNewsByCategoryOnly(selectedCategory!!)
                }

                !selectedCountry.isNullOrEmpty() -> {
                    Log.d("NewsFragment", "we only have country = {$selectedCountry}")

                    // Fetch news filtered by country only
                    newsViewModel.fetchNewsByCountryonly(selectedCountry)
                }

                else -> {
                    Log.d(
                        "NewsFragment",
                        "we don't have category = {$selectedCategory} and country = {$selectedCountry}"
                    )

                    //  If no filters are selected, fetch general news
                    newsViewModel.fetchSource()
                }
            }
        }
    }


    private fun observeRandomNews() {
        newsViewModel.randomNewsLiveData.observe(viewLifecycleOwner) { article ->
            article?.let { newsArticle ->
                bannerSource.text = newsArticle.source.name
                bannerHeader.text = newsArticle.title
                Glide.with(requireContext()).load(newsArticle.urlToImage).into(bannerImage)


                frameCard.setOnClickListener {
                    if (newsArticle.url.isNotEmpty()) {
                        Log.d("NewsFragment", "Opening URL: ${newsArticle.url}")
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newsArticle.url))
                        try {
                            requireContext().startActivity(intent)
                        } catch (e: Exception) {
                            Log.e("NewsFragment", "Error opening URL: ${e.message}")
                        }
                    } else {
                        Log.e(
                            "NewsFragment",
                            "Attempted to open URL but found empty: '${article.url}'"
                        )
                        Log.e("NewsFragment", "No URL found for this article")
                    }
                }

            }
        }
    }
}






