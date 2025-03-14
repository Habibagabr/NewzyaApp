import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.habiba.newsapp.R
import com.habiba.newsapp.countries
import com.habiba.newsapp.countries.Companion.countryMap
import com.habiba.newsapp.responce.Article
import com.habiba.newsapp.responce.SourceX

class NewsRecyclerView(
    private var articles: List<Article>,
    private var sources: MutableList<SourceX>  ,
    private var countriesMap: Map<String,String> = countryMap
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

        Log.d("NewsRecyclerView", "🔹 Binding ViewHolder - Position: $position, Title: ${newsItem.title}")

        Glide.with(holder.itemView.context)
            .load(newsItem.urlToImage)
            .placeholder(R.drawable.newsphoto)
            .error(R.drawable.no_image_found)
            .into(holder.imageNews)

        val sourceId = newsItem.source.id?.trim()?.lowercase() ?: ""
        Log.d("NewsRecyclerView", "🔹 News Item Source ID: '$sourceId'")

        if (sources.isEmpty()) {
            Log.e("NewsRecyclerView", "❌ Sources list is EMPTY! Ensure sources are populated before binding.")
            return
        }

        val sourceDetails = sources.find { it.id?.trim()?.lowercase() == sourceId }

        if (sourceDetails != null) {
            Log.d("NewsRecyclerView", "✅ Found Source: ${sourceDetails.id}, Country: ${sourceDetails.country}, Category: ${sourceDetails.category}")
        } else {
            Log.e("NewsRecyclerView", "❌ Source ID '$sourceId' NOT FOUND in sources list!")
        }

        var countryKey= sourceDetails?.country ?:""
        holder.country.text = countriesMap[countryKey]
        holder.category.text = sourceDetails?.category ?: ""
        holder.headline.text = newsItem.title ?: "No Title"
        holder.source.text = newsItem.source.name ?: "Unknown Source"
        holder.content.text = newsItem.content ?: "No Content"
        holder.date.text = newsItem.publishedAt.substringBefore("T")
    }

    override fun getItemCount(): Int = articles.size

    /** ✅ Update sources and refresh RecyclerView */
    fun updateSources(newSources: List<SourceX>) {
        sources.clear()
        sources.addAll(newSources)
        Log.d("NewsRecyclerView", "✅ Sources Updated! Available Sources: ${sources.map { it.id }}")
        notifyDataSetChanged()  // 🔥 Refresh RecyclerView when sources are updated
    }

    fun updateNews(newArticles: List<Article>) {
        articles = newArticles
        notifyDataSetChanged()  // 🔥 Refresh RecyclerView when news updates
    }

}
