package com.habiba.newsapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.habiba.newsapp.adaptors.FavouritesAdaptor
import com.habiba.newsapp.responce.favouritesData

class favourite : Fragment() {  // Capitalize class name for better naming convention
    private lateinit var favouritesRecyclerView: RecyclerView
    private lateinit var favouritesAdaptor: FavouritesAdaptor
    private val savedArticlesList = mutableListOf<favouritesData>()  // Mutable list to store articles

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment first
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)

        // Initialize RecyclerView
        favouritesRecyclerView = view.findViewById(R.id.favrec)
        favouritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set adapter with an empty list initially
        favouritesAdaptor = FavouritesAdaptor(savedArticlesList)
        favouritesRecyclerView.adapter = favouritesAdaptor

        // Retrieve current user ID
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()

        if (currentUserId != null) {
            db.collection("Favourites")
                .document(currentUserId)
                .get()
                .addOnSuccessListener { document ->
                    val savedArticles = document.get("favoritesArray") as? List<Map<String, String>> ?: emptyList()

                    Log.d("SavedArticlesDebug", "Retrieved ${savedArticles.size} articles:")

                    // Convert the map data into favouritesData objects and update the adapter
                    for (article in savedArticles) {
                        val favouriteItem = favouritesData(
                            content = article["content"],
                            description = article["description"],
                            publishedAt = article["date"],
                            source = article["source"],
                            title = article["title"],
                            url = article["url"],
                            urlToImage = article["imageURL"],
                            category = article["category"],
                            country = article["country"]
                        )
                        Log.d("the image here : "," the image is : ${ article["imageURL"]}")
                        Log.d("the image here: "," the data is : ${article["date"]}")
                        savedArticlesList.add(favouriteItem)
                    }
                    // Notify adapter about the data change
                    savedArticlesList.reverse()
                    favouritesAdaptor.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreError", "Failed to retrieve saved articles", e)
                }
        }

        return view  // Return the inflated view
    }
}
