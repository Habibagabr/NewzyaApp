package com.habiba.newsapp.adaptors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.habiba.newsapp.R
import com.habiba.newsapp.responce.favouritesData
import com.habiba.newsapp.viewmodel.favouritesViewmodel

class FavouritesAdaptor(private var favouriteItems: List<favouritesData>) :
    RecyclerView.Adapter<FavouritesAdaptor.FavouritesAdaptorViewHolder>() {
        private lateinit var favobj:favouritesViewmodel

    // ViewHolder to bind the views from the RecyclerView item layout
    class FavouritesAdaptorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageNews: ImageView = itemView.findViewById(R.id.newsImage)
        val country: TextView = itemView.findViewById(R.id.newsCountry)
        val source: TextView = itemView.findViewById(R.id.newsSource)
        val category: TextView = itemView.findViewById(R.id.newsCategory)
        val headline: TextView = itemView.findViewById(R.id.newsHeadline)
        val content: TextView = itemView.findViewById(R.id.newscontent)
        val date: TextView = itemView.findViewById(R.id.Newsdate)
        val save: ImageView = itemView.findViewById(R.id.save)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesAdaptorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_recyclerview_item, parent, false)
        return FavouritesAdaptorViewHolder(view)
    }

    override fun getItemCount(): Int {
        return favouriteItems.size
    }

    override fun onBindViewHolder(holder: FavouritesAdaptorViewHolder, position: Int) {
        val item = favouriteItems[position]

        //date changing :
        favobj=favouritesViewmodel()


        // Binding the data to the views
        holder.headline.text = item.title
        holder.content.text = item.description
        holder.source.text = item.source
        holder.country.text = item.country
        holder.category.text = item.category
        holder.date.text = item.publishedAt?.substringBefore("T")

        // Loading image with Glide (make sure Glide is added in your dependencies)
        Glide.with(holder.itemView.context)
            .load(item.urlToImage)
            .placeholder(R.drawable.newsphoto) // optional placeholder
            .into(holder.imageNews)
        holder.save.setImageResource(R.drawable.favclicked)

        holder.save.setOnClickListener {
            favobj.deletefavourites(item)
        }


    }

}
