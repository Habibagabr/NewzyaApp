import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.habiba.newsapp.R
import com.habiba.newsapp.countries.Companion.countryMap
import com.habiba.newsapp.responce.Article
import com.habiba.newsapp.responce.SourceX

class NewsRecyclerView(
    private var articles: List<Article>,
    private var sources: MutableList<SourceX>  ,
    private var countriesMap: Map<String,String> = countryMap,
    private var categoryy:String?="general"
) : RecyclerView.Adapter<NewsRecyclerView.NewsRecyclerViewHolder>() {

    class NewsRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageNews: ImageView = itemView.findViewById(R.id.newsImage)
        val country: TextView = itemView.findViewById(R.id.newsCountry)
        val source: TextView = itemView.findViewById(R.id.newsSource)
        val category: TextView = itemView.findViewById(R.id.newsCategory)
        val headline: TextView = itemView.findViewById(R.id.newsHeadline)
        val content: TextView = itemView.findViewById(R.id.newscontent)
        val date:TextView=itemView.findViewById(R.id.Newsdate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_recyclerview_item, parent, false)
        return NewsRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsRecyclerViewHolder, position: Int) {
        val newsItem = articles[position]

        Log.d("NewsRecyclerView", "Binding ViewHolder - Position: $position, Title: ${newsItem.title}")

        Glide.with(holder.itemView.context)
            .load(newsItem.urlToImage)
            .placeholder(R.drawable.newsphoto)
            .error(R.drawable.no_image_found)
            .into(holder.imageNews)



        val sourceId = newsItem.source.id?.trim()?.lowercase() ?: ""
            Log.d("NewsRecyclerView", " News Item Source ID: '$sourceId'")
        val sourceDetails = sources.find { it.id.trim().lowercase() == sourceId }

        if (sources.isEmpty()) {
            Log.e("NewsRecyclerView", "Sources list is EMPTY! Ensure sources are populated before binding.")
            return
        }

        if (sourceDetails != null) {
                Log.d(
                    "NewsRecyclerView",
                    "Found Source: ${sourceDetails.id}, Country: ${sourceDetails.country}, Category: ${sourceDetails.category}"
                )
            } else {
                Log.e("NewsRecyclerView", "Source ID '$sourceId' NOT FOUND in sources list!")
            }


            var countryKey = sourceDetails?.country ?: ""
            if (countryKey == "")
                holder.country.text = ""
            else {
                holder.country.text = countriesMap[countryKey]
            }
            Log.e("NewsRecyclerView", "country is '${holder.country.text}' ")

            holder.category.text = categoryy
            Log.e("NewsRecyclerView", "category is '$categoryy' ")

            holder.headline.text = newsItem.title ?: "No Title"
            holder.source.text = newsItem.source.name ?: "Unknown Source"
            holder.content.text = newsItem.content ?: "No Content"
            holder.date.text = newsItem.publishedAt.substringBefore("T")
            // 🔹 Open URL when user clicks on item
            holder.itemView.setOnClickListener {
                val url = newsItem.url  // Get URL from API response
                if (!url.isNullOrEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    holder.itemView.context.startActivity(intent)  // Open the article in a browser
                } else {
                    Log.e("NewsRecyclerView", "No URL found for this article")
                }
            }

    }

    override fun getItemCount(): Int = articles.size

    /** Update sources and refresh RecyclerView */
    fun updateSources(newSources: List<SourceX?>) {
        sources.clear()
        sources.addAll(newSources.filterNotNull()) // Remove null values before adding
        Log.d("NewsRecyclerView", " Sources Updated! Available Sources: ${sources.map { it.id }}")
        notifyDataSetChanged()  //  Refresh RecyclerView when sources are updated
    }

    fun updateNews(newArticles: List<Article>, category: String = "general") {
        articles = newArticles
        categoryy = category  // ✅ Update category in adapter
        Log.d("NewsRecyclerView", "Updated category to: $categoryy")
        notifyDataSetChanged()
    }


}
