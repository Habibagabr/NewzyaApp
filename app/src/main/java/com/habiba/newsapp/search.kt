package com.habiba.newsapp

import SearchNewsAdaptor
import SearchViewModelFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.habiba.newsapp.R
import com.habiba.newsapp.repository.repository
import com.habiba.newsapp.responce.Article
import com.habiba.newsapp.viewmodel.SearchViewModel

class search : Fragment() {

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var newsAdapter: SearchNewsAdaptor
    private lateinit var frame: ConstraintLayout
    private lateinit var animtext: TextView
    private lateinit var animation: LottieAnimationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        frame=view.findViewById(R.id.animcons)
        animtext=view.findViewById(R.id.animtext)
        animation=view.findViewById(R.id.lottieAnimationView)
        frame.visibility=View.VISIBLE
        val newsRepository = repository()
        val factory = SearchViewModelFactory(newsRepository)
        searchViewModel = ViewModelProvider(this, factory)[SearchViewModel::class.java]

        //  Initialize UI components
        setupRecyclerView(view)
        setupSearchView(view)

        return view
    }

    private fun setupRecyclerView(view: View) {
        searchRecyclerView = view.findViewById(R.id.SearchRecyclerView)
        searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        //  Initialize RecyclerView Adapter with an empty list
        newsAdapter = SearchNewsAdaptor(emptyList())
        searchRecyclerView.adapter = newsAdapter
    }

    private fun setupSearchView(view: View) {
        val searchView = view.findViewById<SearchView>(R.id.searchView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false  // We no longer need explicit submission
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.length >= 3) {  // Avoid API calls for very short queries
                        searchViewModel.searchNews(it)
                    }
                }
                return true
            }
        })

        //  Observe search results and update RecyclerView dynamically
        searchViewModel.searchResults.observe(viewLifecycleOwner) { searchResults ->
            if (searchResults.isNullOrEmpty()) {
                Log.d("SearchFragment", " No results found")
                //no result
                frame.visibility=View.VISIBLE
                animtext.text="No result found"
                ////loading animation
                animation=view.findViewById(R.id.lottieAnimationView)
                animation.setAnimation(R.raw.notfound)
                newsAdapter.updateNews(emptyList())



            } else {
                Log.d("SearchFragment", "Updating RecyclerView with ${searchResults.size} results")
                frame.visibility=View.GONE
                newsAdapter.updateNews(searchResults)  // Update RecyclerView in real-time
            }
        }
    }

}
