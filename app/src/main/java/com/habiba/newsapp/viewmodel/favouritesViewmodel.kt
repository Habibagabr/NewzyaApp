package com.habiba.newsapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.habiba.newsapp.constants
import com.habiba.newsapp.favourite
import com.habiba.newsapp.responce.Article
import com.habiba.newsapp.responce.favouritesData

class favouritesViewmodel:ViewModel() {

    fun addfavourites(newsArticle: favouritesData, userId: String? = FirebaseAuth.getInstance().currentUser?.uid)
    {
        Log.d("firebase", "user id = ${userId}")

        val db = FirebaseFirestore.getInstance()
        val favData = hashMapOf(
            "URL" to newsArticle.url,
            "category" to newsArticle.category,
            "country" to newsArticle.country,
            "date" to newsArticle.publishedAt,
            "imageURL" to newsArticle.urlToImage,
            "source" to newsArticle.source,
            "title" to newsArticle.title,
            "description" to newsArticle.description
        )

        if (userId != null) {
            db.collection("Favourites")
                .document(userId)
                .update("favoritesArray", FieldValue.arrayUnion(favData)) // Append to array
                .addOnSuccessListener {
                    Log.d("Firestore", "Favorite article added successfully!")
                }
                .addOnFailureListener { e ->
                    // Handle the case where the document doesn't exist (first time user)
                    db.collection("Favourites")
                        .document(userId)
                        .set(mapOf("favoritesArray" to listOf(favData))) // Create the array if it doesn't exist
                        .addOnSuccessListener {
                            Log.d("Firestore", "Created new document with favorites array!")
                        }
                        .addOnFailureListener { e2 ->
                            Log.w("Firestore", "Error writing document", e2)
                        }
                }
        }
    }

    fun deletefavourites(newsArticle: favouritesData,userID: String =constants.userID)
    {
          val currentUserId= FirebaseAuth.getInstance().currentUser?.uid
          val db=FirebaseFirestore.getInstance()
        if (currentUserId != null) {
            db.collection("Favourites")
                .document(currentUserId)
                .get()
                .addOnSuccessListener { document->
                    if (document.exists()) {
                        val savedArticles =
                            document.get("favoritesArray") as? MutableList<Map<String, String>> ?: mutableListOf()
                        val updatedArticles = savedArticles.filter { it["title"] != newsArticle.title
                        }
                        db.collection("Favourites").document(currentUserId)
                            .update("favoritesArray", updatedArticles)
                            .addOnSuccessListener {
                                Log.d("FirestoreDelete", "Article removed from favoritesArray!")
                            }
                            .addOnFailureListener { e ->
                                Log.w("FirestoreDelete", "Error updating document", e)
                            }
                    }
                }
                    }
        }
    }

