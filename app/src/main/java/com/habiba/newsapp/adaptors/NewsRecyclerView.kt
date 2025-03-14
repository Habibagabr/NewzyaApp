import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.habiba.newsapp.R
import com.habiba.newsapp.responce.Article

class NewsRecyclerView(
    private var articles: List<Article>,
    private val selectedCountry: String?,
    private val selectedCategory: String?
) : RecyclerView.Adapter<NewsRecyclerView.NewsRecyclerViewHolder>() {

    class NewsRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageNews: ImageView = itemView.findViewById(R.id.newsImage)
        val country: TextView = itemView.findViewById(R.id.newsCountry)
        val source: TextView = itemView.findViewById(R.id.newsSource)
        val category: TextView = itemView.findViewById(R.id.newsCategory)
        val headline: TextView = itemView.findViewById(R.id.newsHeadline)
        val content: TextView = itemView.findViewById(R.id.newscontent)
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
            .into(holder.imageNews)

        holder.country.text = selectedCountry
        holder.category.text = selectedCategory
        holder.headline.text = newsItem.title
        holder.source.text = newsItem.source?.name
        holder.content.text = newsItem.content
    }


    override fun getItemCount(): Int = articles.size

    fun updateNews(newArticles: List<Article>) {
        Log.d("NewsRecyclerView", "Updating RecyclerView with ${newArticles.size} articles")
        this.articles = newArticles
        notifyDataSetChanged()
    }


}